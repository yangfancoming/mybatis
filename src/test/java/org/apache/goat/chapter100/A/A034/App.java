package org.apache.goat.chapter100.A.A034;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Bar;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A032/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   * 别名方式三：   批量起别名

   */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Bar bar = sqlSession.selectOne("org.apache.goat.chapter100.A032.App.selectById",1);
    System.out.println(bar);
  }

}
