
package org.apache.ibatis.datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.reflection.ExceptionUtil;


class PooledConnection implements InvocationHandler {

  private static final String CLOSE = "close";
  private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

  private final int hashCode;
  //记录当前连接所在的数据源对象，本次连接是由这个数据源创建的，关闭后也是回到这个数据源；
  private final PooledDataSource dataSource;
  //真正的连接对象
  private final Connection realConnection;
  //连接的代理对象
  private final Connection proxyConnection;
  //从数据源取出来连接的时间戳
  private long checkoutTimestamp;
  //连接创建的的时间戳
  private long createdTimestamp;
  //连接最后一次使用的时间戳
  private long lastUsedTimestamp;
  //根据数据库url、用户名、密码生成一个hash值，唯一标识一个连接池
  private int connectionTypeCode;
  //连接是否有效
  private boolean valid;

  /**
   * Constructor for SimplePooledConnection that uses the Connection and PooledDataSource passed in.
   * //在构造方法中将真正的连接对象connection保存到内部的realConnection属性
   * @param connection - the connection that is to be presented as a pooled connection
   * @param dataSource - the dataSource that the connection is from
   */
  public PooledConnection(Connection connection, PooledDataSource dataSource) {
    this.hashCode = connection.hashCode();
    this.realConnection = connection;
    this.dataSource = dataSource;
    this.createdTimestamp = System.currentTimeMillis();
    this.lastUsedTimestamp = System.currentTimeMillis();
    this.valid = true;
    this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
  }

  /**
   * Invalidates the connection.
   */
  public void invalidate() {
    valid = false;
  }

  /**
   * Method to see if the connection is usable.
   *
   * @return True if the connection is usable
   */
  public boolean isValid() {
    return valid && realConnection != null && dataSource.pingConnection(this);
  }

  /**
   * Getter for the *real* connection that this wraps.
   * @return The connection
   */
  public Connection getRealConnection() {
    return realConnection;
  }

  /**
   * Getter for the proxy for the connection.
   * @return The proxy
   */
  public Connection getProxyConnection() {
    return proxyConnection;
  }

  /**
   * Gets the hashcode of the real connection (or 0 if it is null).
   * @return The hashcode of the real connection (or 0 if it is null)
   */
  public int getRealHashCode() {
    return realConnection == null ? 0 : realConnection.hashCode();
  }

  /**
   * Getter for the connection type (based on url + user + password).
   * @return The connection type
   */
  public int getConnectionTypeCode() {
    return connectionTypeCode;
  }

  /**
   * Setter for the connection type.
   * @param connectionTypeCode - the connection type
   */
  public void setConnectionTypeCode(int connectionTypeCode) {
    this.connectionTypeCode = connectionTypeCode;
  }

  /**
   * Getter for the time that the connection was created.
   * @return The creation timestamp
   */
  public long getCreatedTimestamp() {
    return createdTimestamp;
  }

  /**
   * Setter for the time that the connection was created.
   * @param createdTimestamp - the timestamp
   */
  public void setCreatedTimestamp(long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  /**
   * Getter for the time that the connection was last used.
   * @return - the timestamp
   */
  public long getLastUsedTimestamp() {
    return lastUsedTimestamp;
  }

  /**
   * Setter for the time that the connection was last used.
   * @param lastUsedTimestamp - the timestamp
   */
  public void setLastUsedTimestamp(long lastUsedTimestamp) {
    this.lastUsedTimestamp = lastUsedTimestamp;
  }

  /**
   * Getter for the time since this connection was last used.
   * @return - the time since the last use
   */
  public long getTimeElapsedSinceLastUse() {
    return System.currentTimeMillis() - lastUsedTimestamp;
  }

  /**
   * Getter for the age of the connection.
   * @return the age
   */
  public long getAge() {
    return System.currentTimeMillis() - createdTimestamp;
  }

  /**
   * Getter for the timestamp that this connection was checked out.
   * @return the timestamp
   */
  public long getCheckoutTimestamp() {
    return checkoutTimestamp;
  }

  /**
   * Setter for the timestamp that this connection was checked out.
   * @param timestamp the timestamp
   */
  public void setCheckoutTimestamp(long timestamp) {
    this.checkoutTimestamp = timestamp;
  }

  /**
   * Getter for the time that this connection has been checked out.
   * @return the time
   */
  public long getCheckoutTime() {
    return System.currentTimeMillis() - checkoutTimestamp;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  /**
   * Allows comparing this connection to another.
   * @param obj - the other connection to test for equality
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PooledConnection) {
      return realConnection.hashCode() == ((PooledConnection) obj).realConnection.hashCode();
    } else if (obj instanceof Connection) {
      return hashCode == obj.hashCode();
    } else {
      return false;
    }
  }

  /**
   * Required for InvocationHandler implementation.
   * @param proxy  - not used
   * @param method - the method to be executed
   * @param args   - the parameters to be passed to the method
   * @see java.lang.reflect.InvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])
   * 总结代理模式对真正的连接对象所做的增强：
   * 1.如果是调用close方法，那么就把连接回收
   * 2.如果调用的不是close，并且是自己定义的方法(不是Object类定义的方法)，那么就在调用这个方法之前检查下连接是否合法，合法再调用这个方法，不合法就抛异常
   * 3.如果方法是Object定义的，那么就不检查是否合法，直接调用
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();
    //1.对于PooledDataSource，如果realConnection调用了close方法，那么就回收该连接，并不是直接关闭
    if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
      dataSource.pushConnection(this);
      return null;
    }
    try {
      //2.如果不是close方法，也不是Object类定义的方法，那么在执行这个方法之前检查下连接还是否合法，只通过valid检查，
      //如果不合法的话，就跑出异常了，
      if (!Object.class.equals(method.getDeclaringClass())) {
        // issue #579 toString() should never fail
        // throw an SQLException instead of a Runtime
        checkConnection();
      }
      //3.如果是Object类定义的方法，那么就直接执行，比如toString方法，不会检查valid，总会成功的。
      return method.invoke(realConnection, args);
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }

  }

  private void checkConnection() throws SQLException {
    if (!valid) {
      throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
    }
  }

}
