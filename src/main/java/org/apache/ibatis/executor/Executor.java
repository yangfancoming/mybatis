
package org.apache.ibatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/** Executor是mybatis为了封装语句执行、调用结果集解析的核心接口。
 * mybatis 执行器分几种：
 *  1. 复用的  ReuseExecutor
 *  2. 简单的  SimpleExecutor  （默认）
 *  3. 批量的  BatchExecutor
*/
public interface Executor {

  ResultHandler NO_RESULT_HANDLER = null;

  int update(MappedStatement ms, Object parameter) throws SQLException;

  // 查询，带分页，带缓存
  <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

  // 查询，带分页
  <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

  // 查询存储过程
  <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

  //刷新批处理语句
  List<BatchResult> flushStatements() throws SQLException;

  // 事务提交
  void commit(boolean required) throws SQLException;

  // 事务回滚
  void rollback(boolean required) throws SQLException;

  // 创建缓存的key
  CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

  // 是否缓存
  boolean isCached(MappedStatement ms, CacheKey key);

  // 清空缓存
  void clearLocalCache();

  // 延迟加载
  void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

  // 获取事务
  Transaction getTransaction();

  void close(boolean forceRollback);

  boolean isClosed();

  void setExecutorWrapper(Executor executor);

}
