
package org.apache.ibatis.executor;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 CachingExecutor的构造函数需要一个Executor参数，然后CachingExecutor对这个executor做功能增强，
 给它添加上缓存特性。我们遇到第三种设计模式，它就是装饰器模式（Decorator），
 它的意图描述为：动态的给一个对象添加一些额外的职责。

 Mybatis 提供的二级缓存是应用级别的缓存，它的生命周期和应用程序的生命周期相同，且与二级缓存相关的配置有以下 3 个：
 1. mybatis-config.xml 配置文件中的 cacheEnabled 配置，它是二级缓存的总开关，只有该配置为 true ，后面的缓存配置才会生效。默认为 true，即二级缓存默认是开启的。
 2. Mapper.xml 配置文件中配置的 <cache> 和 <cache-ref>标签，如果 Mapper.xml 配置文件中配置了这两个标签中的任何一个，则表示开启了二级缓存的功能，在 Mybatis 解析 SQL 源码分析一 文章中已经分析过，如果配置了 <cache> 标签，则在解析配置文件的时候，会为该配置文件指定的 namespace 创建相应的 Cache 对象作为其二级缓存（默认为 PerpetualCache 对象），如果配置了 <cache-ref> 节点，则通过 ref 属性的namespace值引用别的Cache对象作为其二级缓存。通过 <cache> 和 <cache-ref> 标签来管理其在namespace中二级缓存功能的开启和关闭
 3. <select> 节点中的 useCache 属性也可以开启二级缓存，该属性表示查询的结果是否要存入到二级缓存中，该属性默认为 true，也就是说 <select> 标签默认会把查询结果放入到二级缓存中。

 CacheExecutor有一个重要属性delegate，它保存的是某类普通的Executor，值在构照时传入。（装饰器模式）
 执行数据库update操作时，它直接调用delegate的update方法，执行query方法时先尝试从cache中取值，
 取不到再调用delegate的查询方法，并将查询结果存入cache中
*/
public class CachingExecutor implements Executor {

  private static final Log log = LogFactory.getLog(CachingExecutor.class);

  //  装饰者(CachingExecutor)持有被装饰者(SimpleExecutor)的引用！！！
  public final Executor delegate; // SimpleExecutor -modify

  private final TransactionalCacheManager tcm = new TransactionalCacheManager();

  public CachingExecutor(Executor delegate) {
    log.warn("进入 【CachingExecutor】 构造函数 {}");
    this.delegate = delegate;
    delegate.setExecutorWrapper(this);
  }

  private void flushCacheIfRequired(MappedStatement ms) {
    Cache cache = ms.getCache();
    if (cache != null && ms.isFlushCacheRequired()) {
      tcm.clear(cache);
    }
  }

  private void ensureNoOutParams(MappedStatement ms, BoundSql boundSql) {
    if (ms.getStatementType() == StatementType.CALLABLE) {
      for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
        if (parameterMapping.getMode() != ParameterMode.IN) {
          throw new ExecutorException("Caching stored procedures with OUT params is not supported.  Please configure useCache=false in " + ms.getId() + " statement.");
        }
      }
    }
  }

  //---------------------------------------------------------------------
  // Implementation of 【Executor】 interface
  //---------------------------------------------------------------------

  @Override
  public Transaction getTransaction() {
    return delegate.getTransaction();
  }

  @Override
  public void close(boolean forceRollback) {
    try {
      // issues #499, #524 and #573
      if (forceRollback) {
        tcm.rollback();
      } else {
        tcm.commit();
      }
    } finally {
      delegate.close(forceRollback);
    }
  }

  @Override
  public boolean isClosed() {
    return delegate.isClosed();
  }

  @Override
  public int update(MappedStatement ms, Object parameterObject) throws SQLException {
    flushCacheIfRequired(ms);// 是否需要更缓存
    return delegate.update(ms, parameterObject);// 更新数据
  }

  @Override
  public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    // 获取 SQL
    BoundSql boundSql = ms.getBoundSql(parameterObject);
    // 创建缓存key，在CacheKey中已经分析过创建过程
    CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
    return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
  }

  @Override
  public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
    flushCacheIfRequired(ms);
    return delegate.queryCursor(ms, parameter, rowBounds);
  }

  @Override
  public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    // 获取查询语句所在namespace对应的二级缓存
    Cache cache = ms.getCache();
    // 是否开启了二级缓存
    if (cache != null) {
      // 根据 <select> 的属性 useCache 的配置，决定是否需要清空二级缓存
      flushCacheIfRequired(ms);
      if (ms.isUseCache() && resultHandler == null) {
        // 二级缓存不能保存输出参数，否则抛异常
        ensureNoOutParams(ms, boundSql);
        // 从二级缓存中查询对应的值
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) tcm.getObject(cache, key);
        if (list == null) {
          // 如果二级缓存没有命中，则调用底层的 Executor 查询，其中会先查询一级缓存，一级缓存也未命中，才会去查询数据库
          list = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);// doit Value 'resultHandler' is always 'null'
          // 查询到的数据放入到二级缓存中去
          tcm.putObject(cache, key, list); // issue #578 and #116
        }
        return list;
      }
    }
    return delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
  }

  @Override
  public List<BatchResult> flushStatements() throws SQLException {
    return delegate.flushStatements();
  }

  @Override
  public void commit(boolean required) throws SQLException {
    delegate.commit(required);
    tcm.commit();
  }

  @Override
  public void rollback(boolean required) throws SQLException {
    try {
      delegate.rollback(required);
    } finally {
      if (required) {
        tcm.rollback();
      }
    }
  }

  @Override
  public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
    return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
  }

  @Override
  public boolean isCached(MappedStatement ms, CacheKey key) {
    return delegate.isCached(ms, key);
  }

  @Override
  public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
    delegate.deferLoad(ms, resultObject, property, key, targetType);
  }

  @Override
  public void clearLocalCache() {
    delegate.clearLocalCache();
  }

  @Override
  public void setExecutorWrapper(Executor executor) {
    throw new UnsupportedOperationException("This method should not be called");
  }
}
