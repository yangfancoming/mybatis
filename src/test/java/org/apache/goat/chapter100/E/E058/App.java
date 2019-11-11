package org.apache.goat.chapter100.E.E058;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E058/mybatis-config.xml";

  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    //只查询用户名时 where 1 = 1 and id = ?
    SysUser query = new SysUser();
    query.setId(1L);
    query.setUserName("admin");
    SysUser user = userMapper.selectByIdOrUserName(query);
    System.out.println(user);
    //当没有 id 时 where 1 = 1 and user_name = ?
    query.setId(null);
    user = userMapper.selectByIdOrUserName(query);
    System.out.println(user);
    //当 id 和 name 都为空时 where 1 = 1 limit 0
    query.setUserName(null);
    user = userMapper.selectByIdOrUserName(query);
    System.out.println(user);
  }

  @Test
  void deleteById2() throws Exception  {
    setUpByReader(XMLPATH);

  }

}
