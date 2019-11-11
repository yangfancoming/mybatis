package org.apache.goat.chapter100.A.A038;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A038/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   * 使用  <environments default="pro_mysql"> 标签 会执行
   * <select id="selectById" parameterType="int" resultType="org.apache.goat.common.Foo"  databaseId="mysql">
   *
   * 使用  <environments default="dev_hsqldb"> 标签 会执行
   * <select id="selectById" parameterType="int" resultType="org.apache.goat.common.Foo"  databaseId="hsqldb">
   *
   * 局部xml中的两个sql 标签是一样的  mybatis 根据 databaseId 属性不同 进行区分
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }





}
