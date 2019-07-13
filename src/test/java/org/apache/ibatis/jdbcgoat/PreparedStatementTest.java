package org.apache.ibatis.jdbcgoat;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by 64274 on 2019/7/13.
 *
 * @ Description: 由于 想要在sql拼串中传入参数 容易造成sql注入，由此为了方便传递参数  PreparedStatement 应运而生
 * @ author  山羊来了
 * @ date 2019/7/13---15:02
 */
public class PreparedStatementTest extends Base {

  String sql ="select * from foo where id = ? and firstname = ?";

  @Test  //
  public void test() throws SQLException {

    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1,1); // 使用 1 替换掉sql中的第一个 ？
    preparedStatement.setString(2,"Jane");  // 使用 Jane 替换掉sql中的第二个 ？
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()){ // 根据列名 获取数据
      System.out.println( resultSet.getObject("id") + "---" + resultSet.getObject("firstname") + "---" + resultSet.getObject("lastname") );
    }
  }

}
