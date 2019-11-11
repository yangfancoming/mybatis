package org.apache.goat.chapter100.C.C030;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.jupiter.api.Test;

import java.util.Date;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C004/mybatis-config.xml";

  /**
   * 对应局部xml文件 #{createTime, jdbcType=TIMESTAMP}
   * 成功插入记录后其值为 2019-11-11 15:28:06
  */
  @Test
  void insert1() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    //创建一个 user 对象
    SysUser user = new SysUser();
    user.setUserName("test1");
    user.setUserPassword("123456");
    user.setUserEmail("test@mybatis.tk");
    user.setUserInfo("test info");
    //正常情况下应该读入一张图片存到 byte 数组中
    user.setHeadImg(new byte[]{1,2,3});
    user.setCreateTime(new Date());
    //将新建的对象插入数据库中，特别注意，这里的返回值 result 是执行的 SQL 影响的行数
    int result = userMapper.insert(user);
    System.out.println(result);
  }

  /**
   * 对应局部xml文件 #{createTime, jdbcType=DATE}
   * 成功插入记录后其值为 2019-11-11 00:00:00
   */
  @Test
  void insert2() throws Exception  {
    setUpByReader(XMLPATH);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    //创建一个 user 对象
    SysUser user = new SysUser();
    user.setUserName("test2");
    user.setUserPassword("123456");
    user.setUserEmail("test@mybatis.tk");
    user.setUserInfo("test2 info");
    //正常情况下应该读入一张图片存到 byte 数组中
    user.setHeadImg(new byte[]{1,2,3});
    user.setCreateTime(new Date());
    //将新建的对象插入数据库中，特别注意，这里的返回值 result 是执行的 SQL 影响的行数
    int result = userMapper.insert2(user);
    System.out.println(result);
  }

}
