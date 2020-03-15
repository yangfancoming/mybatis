package org.apache.goat.chapter100.A.A025.item01;

import org.apache.common.MyBaseDataTest;
import org.junit.Test;

/**
 * Created by Administrator on 2020/3/15.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/15---13:14
 */
public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A025/mybatis-config.xml";

  @Test
  public void test1() throws Exception {
    setUpByReader(XMLPATH);
    UsersMapper usersMapper = sqlSession.getMapper(UsersMapper.class);
    Users user = usersMapper.getUser(1);
    System.out.println(user);
  }
}
