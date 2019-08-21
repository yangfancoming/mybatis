package org.apache.goat.chapter100.A032;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A032/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   * 别名方式二：   批量起别名
   * 方式一 单个起别名的缺点： 每个类都要一个  <typeAlias type="org.apache.goat.common.Foo" alias="Foo"/> 标签
   * 显示 很繁琐，因此 方式二 应运而生
   *
   * doit 查看源码 为啥 前三种情况都可以？
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL); // Foo 可以
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }

  @Test
  void test2() throws Exception {
    setUpByReader(XMLPATH,DBSQL);  // foo 可以
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById2",1);
    System.out.println(foo);
  }

  @Test
  void test3() throws Exception {
    setUpByReader(XMLPATH,DBSQL);  // FOO 可以
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById3",1);
    System.out.println(foo);
  }

  @Test
  void test4() throws Exception {
    setUpByReader(XMLPATH,DBSQL);  // FOO1  报错！
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById43",1);
    System.out.println(foo);
  }
}
