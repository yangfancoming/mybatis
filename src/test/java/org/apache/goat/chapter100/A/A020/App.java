package org.apache.goat.chapter100.A.A020;

import org.apache.goat.MyBaseDataTest;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A020/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   *  <properties> 标签从外部配置文件读取属性值
   * 读取的是  resources/dbconfig.properties 路径下 属性文件
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
//    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
//    System.out.println(foo);
  }



}
