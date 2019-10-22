package org.apache.goat.chapter900.A010;

import org.junit.jupiter.api.Test;

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

  @Test
  public void test(){
    //SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '=1 or '1' = '1'
    String user = "111";
    String password = "222";
    String sql = "SELECT user,password FROM user_table WHERE user = '"+ user +"' AND password = '"+ password +"'";
    System.out.println(sql);
  }
}
