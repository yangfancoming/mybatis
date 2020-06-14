package org.apache.goat.chapter100.A.A025.item01;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.junit.Test;

/**
 * Created by Administrator on 2020/3/15.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/15---13:14
 *
 * 源码位置
 * @see XMLConfigBuilder#typeHandlerElement(org.apache.ibatis.parsing.XNode)
 */
public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A025/mybatis-config.xml";

  @Test
  public void test1() throws Exception {
    setUpByReader(XMLPATH);
    UsersMapper usersMapper = sqlSession.getMapper(UsersMapper.class);
    Users user = usersMapper.getUser(3);
    System.out.println(user.getSex1().getName());
  }

  // doit 为啥失败
  @Test
  public void test2() throws Exception {
    setUpByReader(XMLPATH);
    UsersMapper usersMapper = sqlSession.getMapper(UsersMapper.class);
    Users users = new Users();
    users.setName("goat");
    users.setPassword("123");
    users.setSex1(SexEnum.male);
    System.out.println(usersMapper.addUser(users));
  }
}
