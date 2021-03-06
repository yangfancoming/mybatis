
package org.apache.ibatis.executor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.apache.ibatis.executor.ExecutionPlaceholder.EXECUTION_PLACEHOLDER;


/**
 * BaseExecutor 是一个抽象类，实现了 Executor 接口，并提供了大部分方法的实现，
 * 只有 4 个基本抽象方法：doUpdate,  doQuery,  doQueryCursor,  doFlushStatement 没有实现，
 * 由子类实现，这 4 个方法相当于模板方法中变化的那部分。Mybatis 的一级缓存就是在该类中实现的。
 *
 *  具体实现类有四个 BaseExecutor、 CachingExecutor、 BatchExecutor、 ReuseExecutor和SimpleExecutor。
 *  具体选用哪个实现，可以在Mybatis的配置文件中进行配，配置如下：
 * <settings>
 *     <setting name="defaultExecutorType" value="REUSE"/>   SIMPLE、REUSE、BATCH
 * </settings>
 * 配置之后在 Configuration 类中的 newExecutor() 函数会选择具体使用的子类。
 *
 * 执行update类型的语句的时候会清空缓存，且执行结果不需要进行缓存，
 * 执行select类型的语句的时候会对数据进行缓存
 */
public abstract class BaseExecutor implements Executor {

  private static final Log log = LogFactory.getLog(BaseExecutor.class);
  // 事务，提交，回滚，关闭事务
  protected Transaction transaction;
  // 底层的 Executor 包装对象 (CachingExecutor)
  protected Executor wrapper;
  // 延迟加载队列 (线程安全队列)
  protected ConcurrentLinkedQueue<DeferredLoad> deferredLoads;
  // 一级缓存，实质就是一个HashMap<Object, Object>，用于缓存查询结果 -modify
  public PerpetualCache localCache;
  // 出参一级缓存，用于缓存输出类型参数（存储过程） ，当statment为callable的时候使用
  protected PerpetualCache localOutputParameterCache;
  protected Configuration configuration;
  // 用来记录嵌套查询的层数
  protected int queryStack;
  private boolean closed;

  protected BaseExecutor(Configuration configuration, Transaction transaction) {
    this.transaction = transaction;
    this.deferredLoads = new ConcurrentLinkedQueue<>();
    this.localCache = new PerpetualCache("LocalCache");
    this.localOutputParameterCache = new PerpetualCache("LocalOutputParameterCache");
    this.closed = false;
    this.configuration = configuration;
    this.wrapper = this;
  }

  // 执行 insert | update | delete 语句，调用 doUpdate 方法实现,在执行这些语句的时候，会清空缓存
  @Override
  public int update(MappedStatement ms, Object parameter) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing an update").object(ms.getId());
    if (closed) throw new ExecutorException("Executor was closed.");
    // 先清局部缓存
    clearLocalCache();
    // 执行SQL语句 由子类实现，模板方法模式
    return doUpdate(ms, parameter);
  }

  // 刷新批处理语句，且执行缓存中还没执行的SQL语句
  @Override
  public List<BatchResult> flushStatements() throws SQLException {
    return flushStatements(false);
  }

  // 刷新批处理语句，且执行缓存中还没执行的SQL语句
  public List<BatchResult> flushStatements(boolean isRollBack) throws SQLException {
    if (closed) throw new ExecutorException("Executor was closed.");
    // doFlushStatements 的 isRollBack 参数表示是否执行缓存中的SQL语句，false表示执行，true表示不执行
    return doFlushStatements(isRollBack);
  }

  // SqlSession.selectList会调用此方法
  @Override
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    // 获取查询SQL   1.根据具体传入的参数，动态地生成需要执行的SQL语句，用BoundSql对象表示
    BoundSql boundSql = ms.getBoundSql(parameter);
    // 创建缓存的key  2.为当前的查询创建一个缓存Key 创建缓存的key，创建逻辑在 CacheKey中已经分析过了
    CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
    // 执行查询
    return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
  }

  // 它封装了缓存逻辑，如果缓存中无法找到，则从数据库中查询，而具体的查询实现doQuery被延迟到了子类来实现
  // 先清局部缓存，再查询，但仅仅查询堆栈为0才清，为了处理递归调用
  @SuppressWarnings("unchecked")
  @Override
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
    if (closed) throw new ExecutorException("Executor was closed.");
    /**
     *  由于嵌套查询，这里会查询多次。 第一个查询，并且当前的语句需要刷新缓存，则进行缓存的刷新
     * 如果当前的查询语句设置了清除缓存的属性为true，那么就要把一级缓存清除
     * 当然里面还需要满足queryStack==0的条件，这个条件涉及到了嵌套查询(nested select/query)，如果是嵌套查询的最外层查询（第一个查询），才进行缓存的清理动作，否则不进行。
     * 这里的queryStack是查询的层级，取决于nested select的层数，例如一个Blog有一个Author，一个Author有一个Account，其中Author和Account都使用了嵌套查询，
     * 并且不是延迟加载(fetchType设置)，那么Author查询的时候queryStack就会是1，Account查询的时候queryStack为2。
     * 针对嵌套查询这里就说这么多，后续会专门写一篇嵌套查询原理的文章，包括非延迟加载以及延迟加载的不同情况的处理方式。
    */
    if (queryStack == 0 && ms.isFlushCacheRequired()) {
      // 如果不是嵌套查询，且 <select> 的 flushCache=true 时才会清空缓存
      clearLocalCache();
    }
    List<E> list;
    try {
      queryStack++;// 嵌套查询层数加1 ，这样递归调用到上面的时候就不会再清局部缓存了
      // 首先从一级缓存中进行查询  //根据cachekey从localCache去查
      list = (resultHandler == null) ? (List<E>) localCache.getObject(key) : null;
      if (list != null) {
        // 如果命中缓存，则处理存储过程 // 如果查到localCache缓存，处理localOutputParameterCache输出参数
        handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
      } else {
        // 如果缓存中没有对应的数据，则查数据库中查询数据
        list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
      }
    } finally {
      queryStack--;
    }
    // 最外层的查询已经结束
    if (queryStack == 0) {
      // 延迟加载队列中所有元素 // 所有非延迟的嵌套查询也已经查完了，那么就可以把嵌套查询的结果放入到需要的对象中
      for (DeferredLoad deferredLoad : deferredLoads) {
        deferredLoad.load();
      }
      // issue #601  // 清空延迟加载队列
      deferredLoads.clear();
      if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
        // issue #482  //如果是statement，清本地缓存
        clearLocalCache();
      }
    }
    return list;
  }

  // 查询存储过程
  @Override
  public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
    BoundSql boundSql = ms.getBoundSql(parameter);
    return doQueryCursor(ms, parameter, rowBounds, boundSql);
  }

  @Override
  public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
    if (closed) throw new ExecutorException("Executor was closed.");
    DeferredLoad deferredLoad = new DeferredLoad(resultObject, property, key, localCache, configuration, targetType);
    if (deferredLoad.canLoad()) { // 如果能加载则立即加载，否则加入到延迟加载队列中
      deferredLoad.load();
    } else {
      deferredLoads.add(new DeferredLoad(resultObject, property, key, localCache, configuration, targetType));
    }
  }

  // 创建缓存key
  @Override
  public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
    if (closed) throw new ExecutorException("Executor was closed.");
    CacheKey cacheKey = new CacheKey();
    // 向 updateList 存入id
    cacheKey.update(ms.getId());
    cacheKey.update(rowBounds.getOffset());// 存入offset
    cacheKey.update(rowBounds.getLimit()); // 存入limit
    cacheKey.update(boundSql.getSql()); // 存入sql
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
    // mimic DefaultParameterHandler logic
    for (ParameterMapping parameterMapping : parameterMappings) {
      if (parameterMapping.getMode() != ParameterMode.OUT) {
        Object value;
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) {
          value = boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
          value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
          value = parameterObject;
        } else {
          MetaObject metaObject = configuration.newMetaObject(parameterObject);
          value = metaObject.getValue(propertyName);
        }
        // 存入每一个参数
        cacheKey.update(value);
      }
    }
    if (configuration.getEnvironment() != null) {
      // issue #176  // 存入 environmentId
      cacheKey.update(configuration.getEnvironment().getId());
    }
    return cacheKey;
  }

  @Override
  public boolean isCached(MappedStatement ms, CacheKey key) {
    return localCache.getObject(key) != null;
  }

  // 事务的提交和回滚
  @Override
  public void commit(boolean required) throws SQLException {
    if (closed) throw new ExecutorException("Cannot commit, transaction is already closed");
    clearLocalCache();// 清空缓存
    flushStatements();// 刷新批处理语句，且执行缓存中的QL语句
    if (required)  transaction.commit();
  }

  @Override
  public void rollback(boolean required) throws SQLException {
    if (!closed) {
      try {
        clearLocalCache(); // 清空缓存
        flushStatements(true);// 刷新批处理语句，且不执行缓存中的SQL
      } finally {
        if (required) transaction.rollback();
      }
    }
  }

  @Override
  public void clearLocalCache() {
    if (!closed) {
      localCache.clear();
      localOutputParameterCache.clear();
    }
  }

  // 4 个抽象方法，由子类实现，模板方法中可变部分
  protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;
  protected abstract List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException;
  protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;
  protected abstract <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException;

  protected void closeStatement(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  /**
   * Apply a transaction timeout.
   * @param statement a current statement
   * @throws SQLException if a database access error occurs, this method is called on a closed <code>Statement</code>
   * @since 3.4.0
   * @see StatementUtil#applyTransactionTimeout(Statement, Integer, Integer)
   */
  protected void applyTransactionTimeout(Statement statement) throws SQLException {
    StatementUtil.applyTransactionTimeout(statement, statement.getQueryTimeout(), transaction.getTimeout());
  }

  private void handleLocallyCachedOutputParameters(MappedStatement ms, CacheKey key, Object parameter, BoundSql boundSql) {
    if (ms.getStatementType() != StatementType.CALLABLE) return; // -modify
    final Object cachedParameter = localOutputParameterCache.getObject(key);
    if (cachedParameter != null && parameter != null) {
      final MetaObject metaCachedParameter = configuration.newMetaObject(cachedParameter);
      final MetaObject metaParameter = configuration.newMetaObject(parameter);
      for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
        if (parameterMapping.getMode() != ParameterMode.IN) {
          final String parameterName = parameterMapping.getProperty();
          final Object cachedValue = metaCachedParameter.getValue(parameterName);
          metaParameter.setValue(parameterName, cachedValue);
        }
      }
    }
  }

  // 从数据库查询数据
  private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    /**
     * 在缓存池中添加占位符,此处代码的作用就是为了防止嵌套查询是查询了相同的数据
     * 这里为什么要先占位呢？
     *  回答：嵌套的延迟加载有可能用的是同一个对象，这里说明已经开始查了，但是由于处理嵌套的查询，此查询还没有查完，再次执行嵌套查询，且查询的是相同的东西，那么就不用再查了
     *  举个例子：一个Blog有一个Author，而Author中又嵌套了一个Blog，那么Blog还没有放到缓存中，但是嵌套查询现在查Author，Author中的Blog又是第一个Blog查询的数据，
     *  这里放置一个占位符就是为了说明，这个Blog已经在查询了，结果还没出来而已，不要急，等结果出来了再进行配对。
     */
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
      // 而具体的查询实现doQuery被延迟到了子类来实现
      list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
      // 删除缓冲池中的占位符
      localCache.removeObject(key);
    }
    // 将从数据库查询的结果添加到一级缓存中
    localCache.putObject(key, list);
    // 处理存储过程
    if (ms.getStatementType() == StatementType.CALLABLE) {
      localOutputParameterCache.putObject(key, parameter);
    }
    return list;
  }

  protected Connection getConnection(Log statementLog) throws SQLException {
    Connection connection = transaction.getConnection();
    if (statementLog.isDebugEnabled()) {
      // JDK 动态代理 返回的 connection 是个代理对象
      return ConnectionLogger.newInstance(connection, statementLog, queryStack);
    } else {
      return connection;
    }
  }

  @Override
  public Transaction getTransaction() {
    if (closed) throw new ExecutorException("Executor was closed.");
    return transaction;
  }

  @Override
  public void close(boolean forceRollback) {
    try {
      try {
        rollback(forceRollback);
      } finally {
        if (transaction != null)  transaction.close();
      }
    } catch (SQLException e) {
      // Ignore.  There's nothing that can be done at this point.
      log.warn("Unexpected exception on closing transaction.  Cause: " + e);
    } finally {
      transaction = null;
      deferredLoads = null;
      localCache = null;
      localOutputParameterCache = null;
      closed = true;
    }
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public void setExecutorWrapper(Executor wrapper) {
    this.wrapper = wrapper;
  }

  private static class DeferredLoad {

    private final MetaObject resultObject;
    private final String property;
    private final Class<?> targetType;
    private final CacheKey key;
    private final PerpetualCache localCache;
    private final ObjectFactory objectFactory;
    private final ResultExtractor resultExtractor;

    // issue #781
    public DeferredLoad(MetaObject resultObject,String property,CacheKey key,PerpetualCache localCache,Configuration configuration, Class<?> targetType) {
      this.resultObject = resultObject;
      this.property = property;
      this.key = key;
      this.localCache = localCache;
      this.objectFactory = configuration.getObjectFactory();
      this.resultExtractor = new ResultExtractor(configuration, objectFactory);
      this.targetType = targetType;
    }

    public boolean canLoad() {
      return localCache.getObject(key) != null && localCache.getObject(key) != EXECUTION_PLACEHOLDER;
    }

    public void load() {
      @SuppressWarnings("unchecked")
      // we suppose we get back a List
      List<Object> list = (List<Object>) localCache.getObject(key);
      Object value = resultExtractor.extractObjectFromList(list, targetType);
      resultObject.setValue(property, value);
    }
  }
}
