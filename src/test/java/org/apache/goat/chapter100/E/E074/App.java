package org.apache.goat.chapter100.E.E074;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E074/mybatis-config.xml";

  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1038L);
    map.put("user_email", "1111");
    map.put("user_password", "2222");
    //更新数据
    int i = userMapper.updateByMap(map);
    System.out.println(i);
  }



}
