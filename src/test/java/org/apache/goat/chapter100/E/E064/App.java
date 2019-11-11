package org.apache.goat.chapter100.E.E064;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E064/mybatis-config.xml";

  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    SysUser user = new SysUser();
    //更新 id = 1 的用户
    user.setId(1L);
    //修改邮箱
    user.setUserEmail("test@11111.11");
    //将新建的对象插入数据库中，特别注意，这里的返回值 result 是执行的 SQL 影响的行数
    int result = userMapper.updateByIdSelective(user);
    Assert.assertEquals(1, result);
    //修改邮箱
    user.setUserPassword("11111");
    //将新建的对象插入数据库中，特别注意，这里的返回值 result 是执行的 SQL 影响的行数
    int result2 = userMapper.updateByIdSelective(user);
    Assert.assertEquals(1, result2);

  }



}
