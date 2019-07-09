
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
  //获取Statement
  Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;
  //设置参数
  void parameterize(Statement statement) throws SQLException;
  //批量处理
  void batch(Statement statement) throws SQLException;
  //更新处理
  int update(Statement statement)  throws SQLException;
  //查找处理
  <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

  <E> Cursor<E> queryCursor(Statement statement) throws SQLException;
  //获得BoundSql
  BoundSql getBoundSql();
  //获得ParameterHandler
  ParameterHandler getParameterHandler();

}
