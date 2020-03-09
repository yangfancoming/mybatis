package org.apache.goat.chapter900.A010;

import org.apache.goat.common.model.Customer;
import org.junit.Test;

import java.sql.*;


public class PreparedStatementQueryTest {

  String url = "jdbc:mysql://192.168.211.128:3306/jdbc";
  String user = "root";
  String password = "12345";

	@Test
	public void testGetForList() throws SQLException {
    String sql = "select id,name,email,birth from customers where id < ?";
    Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setObject(1,9);
    ResultSet resultSet = ps.executeQuery();
    if(resultSet.next()){//next():判断结果集的下一条是否有数据，如果有数据返回true,并指针下移；如果返回false,指针不会下移。
      //获取当前这条数据的各个字段值
      int id = resultSet.getInt(1);
      String name = resultSet.getString(2);
      String email = resultSet.getString(3);
      Date birth = resultSet.getDate(4);
      //方式三：将数据封装为一个对象（推荐）
      Customer customer = new Customer(id, name, email, birth);
      System.out.println(customer);
    }
  }


  @Test
  public void test2() throws SQLException {
    String sql = "select id,name,email,birth from customers where id < ?";
    Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setObject(1,9);
    ps.execute(); // mybatis 源码方式！
    ResultSet resultSet = ps.getResultSet();
    if(resultSet.next()){//next():判断结果集的下一条是否有数据，如果有数据返回true,并指针下移；如果返回false,指针不会下移。
      //获取当前这条数据的各个字段值
      int id = resultSet.getInt(1);
      String name = resultSet.getString(2);
      String email = resultSet.getString(3);
      Date birth = resultSet.getDate(4);
      //方式三：将数据封装为一个对象（推荐）
      Customer customer = new Customer(id, name, email, birth);
      System.out.println(customer);
    }
  }

}
