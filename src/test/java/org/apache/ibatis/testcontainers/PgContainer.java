

package org.apache.ibatis.testcontainers;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

public class PgContainer {

  private static final String DB_NAME = "mybatis_test";
  private static final String USERNAME = "u";
  private static final String PASSWORD = "p";
  private static final String DRIVER = "org.postgresql.Driver";

  private static final PostgreSQLContainer<?> INSTANCE = initContainer();

  private static PostgreSQLContainer<?> initContainer() {
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>().withDatabaseName(DB_NAME).withUsername(USERNAME).withPassword(PASSWORD);
    container.start();
    return container;
  }

  public static DataSource getUnpooledDataSource() {
    return new UnpooledDataSource(PgContainer.DRIVER, INSTANCE.getJdbcUrl(), PgContainer.USERNAME, PgContainer.PASSWORD);
  }

  private PgContainer() {
    super();
  }
}
