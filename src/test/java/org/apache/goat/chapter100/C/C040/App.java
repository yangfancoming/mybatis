package org.apache.goat.chapter100.C.C040;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C040/mybatis-config.xml";

  @Test
  void insert1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    //从数据库查询 1 个 user 对象
    SysUser user = userMapper.selectById(1L);
    //当前 userName 为 admin
    Assert.assertEquals("admin", user.getUserName());
    //修改用户名
    user.setUserName("admin_test");
    //修改邮箱
    user.setUserEmail("test@mybatis.tk");
    //更新数据，特别注意，这里的返回值 result 是执行的 SQL 影响的行数
    int result = userMapper.updateById(user);
    //只更新 1 条数据
    Assert.assertEquals(1, result);
    //根据当前 id 查询修改后的数据
    user = userMapper.selectById(1L);
    System.out.println(user);
  }
}
