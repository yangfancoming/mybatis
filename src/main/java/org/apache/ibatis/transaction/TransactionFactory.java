
package org.apache.ibatis.transaction;

import java.util.Properties;
import org.apache.ibatis.session.TransactionIsolationLevel;
import java.sql.Connection;
import javax.sql.DataSource;

/**
 * Creates {@link Transaction} instances.
 */
public interface TransactionFactory {

  /** 设置工厂的属性
   * Sets transaction factory custom properties.
   * @param props
   */
  default void setProperties(Properties props) {
    // NOP
  }

  /** 创建新的事务   需要连接对象
   * Creates a {@link Transaction} out of an existing connection.
   * @param conn Existing database connection
   * @return Transaction
   * @since 3.1.0
   */
  Transaction newTransaction(Connection conn);

  /** 创建事务，需要数据源，事务级别，是否自动提交
   * Creates a {@link Transaction} out of a datasource.
   * @param dataSource DataSource to take the connection from
   * @param level Desired isolation level
   * @param autoCommit Desired autocommit
   * @return Transaction
   * @since 3.1.0
   */
  Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
