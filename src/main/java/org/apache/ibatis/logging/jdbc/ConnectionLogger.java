
package org.apache.ibatis.logging.jdbc;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Connection proxy to add logging.
 * Connection 的代理类，增加了日志功能
 * 负责打印连接信息，和sql语句，并创建PreparedStatementLogger
 */
public final class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

  private static final Log log = LogFactory.getLog(ConnectionLogger.class);
  //真正的连接对象
  private final Connection connection;

  private ConnectionLogger(Connection conn, Log statementLog, int queryStack) {
    super(statementLog, queryStack);
    this.connection = conn;
  }

  // ConnectionLogger.invoke() 方法是代理对象的核心方法，它为 prepareStatement()、prepareCall() 、 createStatement() 等方法提供了代理
  //对连接的增强
  @Override
  public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
    try {
      // 如果调用的是从 Object 继承的方法，则直接调用，不做任何其他处理
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, params);
      }
      log.warn(" 进入 ConnectionLogger 动态代理对象 invoke()方法 ，方法名：【" + method.getName() + "】");
      /**
       如果调 用 的是  prepareStatement()、prepareCall() 、 createStatement()方法，
       则在创建相应 Statement 对象后，为其创建代理对象并返回该代理对象
      */
      if ("prepareStatement".equals(method.getName())) {
        if (isDebugEnabled()) {
          debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
        }
        // 调用底层封装的 Connection 对象的 prepareStatement() 方法，得到 PreparedStatement 对象
        PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
        //为该 PreparedStatement 对象创建代理对象
        stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
        log.warn(" 进入 ConnectionLogger 动态代理对象 invoke()方法 ，返回PreparedStatement对象：【" + stmt + "】");
        return stmt;
      } else if ("prepareCall".equals(method.getName())) {
        if (isDebugEnabled()) {
          debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
        }
        PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
        stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
        return stmt;
      } else if ("createStatement".equals(method.getName())) {
        Statement stmt = (Statement) method.invoke(connection, params);
        stmt = StatementLogger.newInstance(stmt, statementLog, queryStack);
        return stmt;
      } else {
        // 其他方法则直接调用底层 Connection 对象的相应方法
        return method.invoke(connection, params);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

  /**
   * Creates a logging version of a connection.
   * 为其封装的 Connection 对象创建相应的代理对象
   * @param conn - the original connection
   * @return - the connection with logging
   */
  public static Connection newInstance(Connection conn, Log statementLog, int queryStack) {
    InvocationHandler handler = new ConnectionLogger(conn, statementLog, queryStack);
    ClassLoader cl = Connection.class.getClassLoader();
    // 使用JDK动态代理的方式创建代理对象
    return (Connection) Proxy.newProxyInstance(cl, new Class[]{Connection.class}, handler);
  }

  /**
   * return the wrapped connection.
   * @return the connection
   */
  public Connection getConnection() {
    return connection;
  }

}
