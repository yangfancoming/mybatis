package org.apache.goat.chapter100.A.A002;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;




class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A002/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**

   */


  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }




}
