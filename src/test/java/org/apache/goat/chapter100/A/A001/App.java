package org.apache.goat.chapter100.A.A001;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A001/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**
   * 无mapper接口 入门示例
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }
}
