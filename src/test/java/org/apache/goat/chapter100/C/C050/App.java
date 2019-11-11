package org.apache.goat.chapter100.C.C050;

import org.apache.goat.MyBaseDataTest;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C050/mybatis-config.xml";

  @Test
  void insert1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    int i = userMapper.deleteById(1036L);
    System.out.println(i);

  }


}
