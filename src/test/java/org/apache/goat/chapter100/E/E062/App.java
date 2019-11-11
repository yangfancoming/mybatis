package org.apache.goat.chapter100.E.E062;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E062/mybatis-config.xml";

  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    //只查询用户名时
    SysUser query = new SysUser();
    query.setUserName("ad");
    List<SysUser> userList = userMapper.selectByUser(query);
    Assert.assertTrue(userList.size() > 0);
    //只查询用户邮箱时
    query = new SysUser();
    query.setUserEmail("test@mybatis.tk");
    userList = userMapper.selectByUser(query);
    Assert.assertTrue(userList.size() > 0);
    //当同时查询用户名和邮箱时
    query = new SysUser();
    query.setUserName("ad");
    query.setUserEmail("test@mybatis.tk");
    userList = userMapper.selectByUser(query);
    //由于没有同时符合这两个条件的用户，查询结果数为 0
    Assert.assertTrue(userList.size() == 0);
  }



}
