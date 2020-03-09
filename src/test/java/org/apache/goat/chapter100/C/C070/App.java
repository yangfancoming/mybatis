package org.apache.goat.chapter100.C.C070;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


/**
 *    C070 局部xml 之  <parameterMap>
 */
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C070/mybatis-config.xml";

  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH);
    Map<String, Integer> map = new HashMap<>();
    map.put("sexid", 1);
    map.put("usercount", -1);
    sqlSession.selectOne("org.apache.goat.chapter100.C.C070.FooMapper.getUserCount", map);
    Integer result = map.get("usercount");
    System.out.println(result);
  }


  @Test
  void insertUser() throws Exception  {
    setUpByReader(XMLPATH);
    User user = new User();
    user.setUsername("张三dd");
    user.setPassword("dd");
    Object o = sqlSession.insert("org.apache.goat.chapter100.C.C070.FooMapper.insertUser", user);
    System.out.println(o);
  }

}
