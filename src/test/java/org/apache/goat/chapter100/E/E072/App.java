package org.apache.goat.chapter100.E.E072;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.SysUser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E072/mybatis-config.xml";

  /**
   *  调用堆栈：
   * apply:75, ForEachSqlNode (org.apache.ibatis.scripting.xmltags)
   * lambda$apply$15:21, MixedSqlNode (org.apache.ibatis.scripting.xmltags)
   * apply:21, MixedSqlNode (org.apache.ibatis.scripting.xmltags)
   * getBoundSql:32, DynamicSqlSource (org.apache.ibatis.scripting.xmltags)
   * getBoundSql:316, MappedStatement (org.apache.ibatis.mapping)
   * <init>:52, BaseStatementHandler (org.apache.ibatis.executor.statement)
   * <init>:32, PreparedStatementHandler (org.apache.ibatis.executor.statement)
   * <init>:31, RoutingStatementHandler (org.apache.ibatis.executor.statement)
   * newStatementHandler:627, Configuration (org.apache.ibatis.session)
   * doUpdate:45, SimpleExecutor (org.apache.ibatis.executor)
   * update:85, BaseExecutor (org.apache.ibatis.executor)
   * update:94, CachingExecutor (org.apache.ibatis.executor)
   * update:241, DefaultSqlSession (org.apache.ibatis.session.defaults)
   * insert:228, DefaultSqlSession (org.apache.ibatis.session.defaults)
   * execute:55, MapperMethod (org.apache.ibatis.binding)
   * invoke:70, MapperProxy (org.apache.ibatis.binding)
   * test1:32, App (org.apache.goat.chapter100.E.E072)
  */
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
    Assert.assertEquals(2,userMapper.insertList(userList));
  }



}
