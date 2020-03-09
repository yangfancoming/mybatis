package org.apache.goat.chapter100.A.A044;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;


/**
 *    注册方式三 package  批量注册
 */
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A044/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /* xml方式 正常*/
  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("org.apache.goat.chapter100.A.A044.FooMapper.selectById",1);
    System.out.println(foo);
  }


  /* xml方式 正常*/
  @Test
  void test() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  /* 注解方式 正常*/
  @Test
  void test1() throws Exception  {
//    setUpByReader(XMLPATH,DBSQL);
//    ZooMapper zooMapper = sqlSession.getMapper(ZooMapper.class);
//    Zoo zoo = zooMapper.selectById(1);
//    System.out.println(zoo);
  }

}
