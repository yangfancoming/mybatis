
package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps a database connection.
 * Handles the connection lifecycle that comprises: its creation, preparation, commit/rollback and close.
 */
public interface Transaction {

  /** 获得连接
   * Retrieve inner database connection.
   * @return DataBase connection
   */
  Connection getConnection() throws SQLException;

  /** 提交
   * Commit inner database connection.
   */
  void commit() throws SQLException;

  /** 回滚
   * Rollback inner database connection.
   */
  void rollback() throws SQLException;

  /** 关闭连接
   * Close inner database connection.
   */
  void close() throws SQLException;

  /** 获得事务超时时间
   * Get transaction timeout if set.
   */
  Integer getTimeout() throws SQLException;

}
