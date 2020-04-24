package org.apache.goat.chapter100.E.E072;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E072/mybatis-config.xml";

  @Test
  void test() throws Exception  {
    setUpByReader(XMLPATH);
  }

  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    List<SysUser> userList = new ArrayList<>();
    for(int i = 0; i < 2; i++){
      SysUser user = new SysUser();
      user.setUserName("test" + i);
      user.setUserPassword("123456" + i);
      user.setUserEmail("test@mybatis.tk");
      userList.add(user);
    }
    int result = userMapper.insertList(userList);
    System.out.println(result);
  }



}
