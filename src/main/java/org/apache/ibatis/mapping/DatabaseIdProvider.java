
package org.apache.ibatis.mapping;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * Should return an id to identify the type of this database.
 * That id can be used later on to build different queries for each database type This mechanism enables supporting multiple vendors or versions
 */
public interface DatabaseIdProvider {

  default void setProperties(Properties p) {
    // NOP
  }

  String getDatabaseId(DataSource dataSource) throws SQLException;
}
