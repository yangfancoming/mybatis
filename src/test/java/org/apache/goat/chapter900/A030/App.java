package org.apache.goat.chapter900.A030;

import org.junit.Test;

import java.sql.Driver;
import java.util.ServiceLoader;

public class App {

  /**
   *  运行结果：
   * org.hsqldb.jdbc.JDBCDriver
   * org.apache.derby.jdbc.AutoloadedDriver
   * org.postgresql.Driver
   * org.testcontainers.jdbc.ContainerDatabaseDriver
   * com.mysql.jdbc.Driver
   * com.mysql.fabric.jdbc.FabricMySQLDriver
  */
    @Test
    public void testSPI() {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers ) {
            System.out.println(driver.getClass().getName());
        }
    }
}
