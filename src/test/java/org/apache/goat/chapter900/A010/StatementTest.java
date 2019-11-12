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

  String url = "jdbc:mysql://192.168.211.128:3306/jdbc";
  String user = "root";
  String password = "12345";

  @Test
  public void test() throws SQLException {
    //SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '=1 or '1' = '1'
    String sql = "SELECT user,password FROM user_table WHERE user = '"+ user +"' AND password = '"+ password +"'";
    System.out.println(sql);
    Connection conn = DriverManager.getConnection(url, user, password);
    Statement statement = conn.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    System.out.println(resultSet);

  }
}
