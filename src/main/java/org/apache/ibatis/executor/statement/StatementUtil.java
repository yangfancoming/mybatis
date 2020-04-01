
package org.apache.ibatis.executor.statement;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility for {@link java.sql.Statement}.
 * @since 3.4.0
 */
public class StatementUtil {

  private StatementUtil() {
    // NOP
  }

  /**
   * Apply a transaction timeout. Update a query timeout to apply a transaction timeout.
   * @param statement a target statement
   * @param queryTimeout a query timeout
   * @param transactionTimeout a transaction timeout
   * @throws SQLException if a database access error occurs, this method is called on a closed <code>Statement</code>
   */
  public static void applyTransactionTimeout(Statement statement, Integer queryTimeout, Integer transactionTimeout) throws SQLException {
    if (transactionTimeout == null) return;
    Integer timeToLiveOfQuery = null;
    if (queryTimeout == null || queryTimeout == 0) {
      timeToLiveOfQuery = transactionTimeout;
    } else if (transactionTimeout < queryTimeout) {
      timeToLiveOfQuery = transactionTimeout;
    }
    if (timeToLiveOfQuery != null) {
      statement.setQueryTimeout(timeToLiveOfQuery);
    }
  }

}
