
package org.apache.ibatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

/**
 在MyBatis实现了StatementHandler 的有四个类：
 RoutingStatementHandler，这是一个封装类，它不提供具体的实现，只是根据Executor的类型，创建不同的类型StatementHandler。
 SimpleStatementHandler，这个类对应于JDBC的Statement对象，用于没有预编译参数的SQL的运行。
 PreparedStatementHandler 这个用于预编译参数SQL的运行。
 CallableStatementHandler 它将实存储过程的调度。
*/
public interface StatementHandler {

  //获取Statement //该方法会在数据库执行前被调用
  Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

  //设置参数 //该方法在prepare 方法之后执行
  void parameterize(Statement statement) throws SQLException;

  //批量处理  //在全局设置配置defaultExecutorType ＝ ” BATCH ” 时
  void batch(Statement statement) throws SQLException;

  //更新处理
  int update(Statement statement)  throws SQLException;

  //查找处理 //执行SELECT 方法时调用
  <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

  //只会在返回值类型为Cursor<T ＞的查询中被调用，
  <E> Cursor<E> queryCursor(Statement statement) throws SQLException;

  //获得BoundSql
  BoundSql getBoundSql();

  //获得ParameterHandler
  ParameterHandler getParameterHandler();

}
