
package org.apache.ibatis.datasource.unpooled;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class UnpooledDataSourceTest {

  @Test
  void shouldNotRegisterTheSameDriverMultipleTimes() throws Exception {
    // https://code.google.com/p/mybatis/issues/detail?id=430
    UnpooledDataSource dataSource = null;
    dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:multipledrivers", "sa", "");
    dataSource.getConnection().close();
    int before = countRegisteredDrivers();
    dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:multipledrivers", "sa", "");
    dataSource.getConnection().close();
    assertEquals(before, countRegisteredDrivers());
  }

  @Disabled("Requires MySQL server and a driver.")
  @Test
  void shouldRegisterDynamicallyLoadedDriver() throws Exception {
    int before = countRegisteredDrivers();
    ClassLoader driverClassLoader = null;
    UnpooledDataSource dataSource = null;
    driverClassLoader = new URLClassLoader(new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.25.jar!/") });
    dataSource = new UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "");
    dataSource.getConnection().close();
    assertEquals(before + 1, countRegisteredDrivers());
    driverClassLoader = new URLClassLoader(new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.25.jar!/") });
    dataSource = new UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "");
    dataSource.getConnection().close();
    assertEquals(before + 1, countRegisteredDrivers());
  }

  int countRegisteredDrivers() {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    int count = 0;
    while (drivers.hasMoreElements()) {
      drivers.nextElement();
      count++;
    }
    return count;
  }

}
