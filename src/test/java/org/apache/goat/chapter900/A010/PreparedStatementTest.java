package org.apache.goat.chapter900.A010;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by 64274 on 2019/10/22.
 *
 * @ Description: 	使用Statement的弊端：
 * 1.需要拼写sql语句 代码编写过于繁琐
 * 2.存在SQL注入的问题
 * @ author  山羊来了
 * @ date 2019/10/22---20:08
 */
public class PreparedStatementTest {

  String url = "jdbc:mysql://192.168.136.128:3306/jdbc";
  String user = "root";
  String password = "12345";

  @Test
  public void test() throws SQLException, ClassNotFoundException, ParseException {
    Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, user, password);

    //4.预编译sql语句，返回PreparedStatement的实例
    String sql = "insert into customers(name,email,birth)values(?,?,?)";//?:占位符
    PreparedStatement ps = conn.prepareStatement(sql);
    //5.填充占位符
    ps.setString(1, "哪吒");
    ps.setString(2, "nezha@gmail.com");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = sdf.parse("1000-01-01");
    ps.setDate(3, new Date(date.getTime()));
    //6.执行操作
    ps.execute();
  }
}
