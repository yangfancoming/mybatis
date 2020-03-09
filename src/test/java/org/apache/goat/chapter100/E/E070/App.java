package org.apache.goat.chapter100.E.E070;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E070/mybatis-config.xml";

  /**
   * DEBUG:==>  Preparing: select * from mybatis.sys_user where id in ( ? , ? )
   * DEBUG:==> Parameters: 1(Long), 1001(Long)
  */
  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    List<Long> idList = Arrays.asList(1L,1001L);
    List<SysUser> userList = userMapper.selectByIdList(idList);
    System.out.println(userList);
  }



}
