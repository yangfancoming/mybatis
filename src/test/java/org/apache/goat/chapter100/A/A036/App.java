package org.apache.goat.chapter100.A.A036;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A036/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   * 切换   注释一下两个标签  查看运行结果
   * <environments default="pro_mysql">   连接 myslq  数据库 查询
   * <environments default="dev_hsqldb">  连接 hsqldb  数据库 查询
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }



}
