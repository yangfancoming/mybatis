package org.apache.goat.chapter100.C.C050;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C050/mybatis-config.xml";

  /**
   * doit  deleteById1 和 deleteById2  都可以正常运行  难道是mapper接口中 可以进行函数重载？？？
  */
  // 通过主键id删除
  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    int i = userMapper.deleteById(1037L);
    System.out.println(i);
  }

  // 通过实体类进行删除
  @Test
  void deleteById2() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    SysUser user1 = userMapper.selectById(1035L);
    int i = userMapper.deleteById(user1);
    System.out.println(i);
  }
}
