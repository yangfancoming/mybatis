package org.apache.goat.chapter900.A010;


import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by 64274 on 2019/10/20.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/20---16:30
 */
public class App {

  String url = "jdbc:mysql://47.98.148.84:3306/jdbc";
  String user = "root";
  String password = "12345";

  /**
   * 方式一： String url = "jdbc:mysql://localhost:3306/test"; 中 jdbc:mysql:协议
   */
  @Test
  public void test1() throws SQLException {
    // 获取Driver实现类对象  通过new出来
    Driver driver = new com.mysql.jdbc.Driver();
    common(driver);
  }

  /**
   * 方式二：对方式一的迭代:在如下的程序中不出现第三方的api,使得程序具有更好的可移植性
   *  比如方式一中的： Driver driver = new com.mysql.jdbc.Driver();
   */
  @Test
  public void test2() throws Exception {
    // 获取Driver实现类对象：通过反射  解耦！
    Class clazz = Class.forName("com.mysql.jdbc.Driver");
    Driver driver = (Driver) clazz.newInstance();
    common(driver);
  }


  // 方式三：使用DriverManager替换Driver
  @Test
  public void testConnection3() throws Exception {
    Class clazz = Class.forName("com.mysql.jdbc.Driver");
    Driver driver = (Driver) clazz.newInstance();
    // 注册驱动
    DriverManager.registerDriver(driver);
    Connection conn = DriverManager.getConnection(url, user, password);
    System.out.println(conn);
  }

  /**
   * 方式四：可以只是加载驱动，不用显示的注册驱动过了。
   *     相较于方式三，可以省略如下的操作：
   *     		Driver driver = (Driver) clazz.newInstance();
   *     		DriverManager.registerDriver(driver);
   * 	  在mysql的Driver实现类中的static静态代码块！
   * 		  static {
   * 				try {
   * 					java.sql.DriverManager.registerDriver(new Driver());
   *                } catch (SQLException E) {
   * 					throw new RuntimeException("Can't register driver!");
   *        }
   *       }
   *
   *   其实 Class.forName("com.mysql.jdbc.Driver");  此句代码也可以注释
   *   原理见 DriverManager 静态代码块loadInitialDrivers #ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
  */
  @Test
  public void testConnection4() throws Exception {
    // 2.加载Driver 会加载 com.mysql.jdbc.Driver 类
    Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, user, password);
    System.out.println(conn);
  }

  /**
   * 方式五(final版)：将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
   * 此种方式的好处？
   * 1.实现了数据与代码的分离。实现了解耦
   * 2.如果需要修改配置文件信息，可以避免程序重新打包。
  */
  @Test
  public void getConnection5() throws Exception{
    //1.读取配置文件中的4个基本信息
//    InputStream is = App.class.getClassLoader().getResourceAsStream("goatjdbc.properties");
    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("goatjdbc.properties");
    Properties pros = new Properties();
    pros.load(is);
    String user = pros.getProperty("jdbc.username");
    String password = pros.getProperty("jdbc.password");
    String url = pros.getProperty("jdbc.url");
    String driverClass = pros.getProperty("jdbc.driver");
    Class.forName(driverClass);
    Connection conn = DriverManager.getConnection(url, user, password);
    System.out.println(conn);
  }



  public void common(Driver driver) throws SQLException {
    // 3.提供连接需要的用户名和密码
    Properties info = new Properties();
    info.setProperty("user", "root");
    info.setProperty("password", "12345");
    // 4.获取连接
    Connection conn = driver.connect(url, info);
    System.out.println(conn);
  }
}
