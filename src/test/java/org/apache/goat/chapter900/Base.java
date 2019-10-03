package org.apache.goat.chapter900;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.*;

/**
 * Created by 64274 on 2019/7/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/13---15:02
 */
public class Base {

  String url = "jdbc:mysql://192.168.136.128:3306/mybatis?Unicode=true&amp;characterEncoding=utf8&amp;useSSL=false";

  Connection connection = null;
  ResultSet resultSet = null;
  Statement statement = null;

  /** DriverManager 3个参数 构造函数 */
  @BeforeEach
  public void before() throws SQLException {
    connection = DriverManager.getConnection(url, "root","12345");
    statement = connection.createStatement();
    System.out.println(connection); // com.mysql.jdbc.JDBC4Connection@387a8303

  }


  @AfterEach
  public void after() throws SQLException {
    if (resultSet!=null){
      resultSet.close();
    }
    statement.close();
    connection.close();
  }
}
