package org.apache.goat.chapter100.B.B062;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/B/B062/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  @Test
  void deleteById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    int i = fooMapper.deleteById(2);
    System.out.println(i);
  }

}
