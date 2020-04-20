package org.apache.goat.chapter900.A010;

import org.junit.jupiter.api.Test;

import java.sql.*;

/**
 * Created by 64274 on 2019/10/22.
 *
 * @ Description: 	使用Statement的弊端：
 * 1.需要拼写sql语句 代码编写过于繁琐
 * 2.存在SQL注入的问题
 * @ author  山羊来了
 * @ date 2019/10/22---20:08
 */
public class StatementTest {

  String url = "jdbc:mysql://47.98.148.84:3306/jdbc";
  String user = "root";
  String password = "12345";
  String sql = "SELECT user,password FROM user_table WHERE user = '"+ user +"' AND password = '"+ password +"'";

  @Test
  public void test() throws Exception {
    //SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '=1 or '1' = '1'
    Connection conn = DriverManager.getConnection(url, user, password);
    Statement statement = conn.createStatement();
    System.out.println("执行SQL:"+sql);
    ResultSet resultSet = statement.executeQuery(sql);
    System.out.println(resultSet);
  }

  /**
   *  boolean execute = statement.execute(sql);
   * 可见其返回值是Boolean类型的，那么什么时候返回的是true，什么时候返回的是false呢？
   * 首先我们知道boolean execute 允许执行查询语句、更新语句、DDL语句。
   * 返回值为true时，表示执行的是查询语句，可以通过getResultSet方法获取结果；返回值为false时，执行的是更新语句或DDL语句
  */
  @Test
  public void test1() throws Exception {
    Connection conn = DriverManager.getConnection(url, user, password);
    Statement statement = conn.createStatement();
    boolean execute = statement.execute("SELECT * FROM jdbc.user_table");
    System.out.println(execute);
    ResultSet rs = statement.getResultSet();
    rs.last();//移到最后一行
    int count = rs.getRow();
    System.out.println(count);
    rs.beforeFirst();//移到初始位置

  }
}
