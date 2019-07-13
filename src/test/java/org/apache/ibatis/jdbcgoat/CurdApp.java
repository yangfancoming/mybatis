
package org.apache.ibatis.jdbcgoat;


import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Properties;

class CurdApp extends Base {

  String sql ="select * from foo";

  /** DriverManager 2个参数 构造函数 */
  @Test
  void test() throws SQLException {
    Properties properties = new Properties();
    properties.put("user","root");
    properties.put("password","12345");
    connection = DriverManager.getConnection(url, properties);
    System.out.println(connection); // com.mysql.jdbc.JDBC4Connection@387a8303
  }



  // 根据列索引 获取数据
  @Test
  void test1() throws SQLException {
    resultSet = statement.executeQuery(sql);
    while (resultSet.next()){
      System.out.println( resultSet.getObject(1) + "---" + resultSet.getObject(2) );
    }
  }

  // 根据列名 获取数据
  @Test
  void test2() throws SQLException {
    resultSet = statement.executeQuery(sql);
    while (resultSet.next()){ // 根据列名 获取数据
      System.out.println( resultSet.getObject("id") + "---" + resultSet.getObject("firstname") + "---" + resultSet.getObject("lastname") );
    }
  }


  @Test  // insert 测试
  public void insert() throws SQLException {
    String sql ="insert into foo (id, firstname, lastname) values (3, 'Goat', 'jordan'); ";
    int i = statement.executeUpdate(sql);
    System.out.println(i);
  }

  @Test   // update 测试
  public void update() throws SQLException {
    String sql ="update foo set firstname = 'update' where id = 3";
    int i = statement.executeUpdate(sql);
    System.out.println(i);
  }

  @Test // delete 测试
  public void delete() throws SQLException {
    String sql ="delete from foo where id = 3 ";
    int i = statement.executeUpdate(sql);
    System.out.println(i);
  }



}
