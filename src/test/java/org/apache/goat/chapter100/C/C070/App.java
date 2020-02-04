package org.apache.goat.chapter100.C.C070;

import org.apache.goat.MyBaseDataTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


/**
 *    C070 局部xml 之  <parameterMap>
 */
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C070/mybatis-config.xml";

  /* xml方式 正常*/
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



}
