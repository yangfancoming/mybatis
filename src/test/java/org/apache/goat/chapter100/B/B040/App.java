package org.apache.goat.chapter100.B.B040;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/B/B040/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   * 演示  mybatis  无 mapper 接口类的方式
   * 缺点： 插入的参数 类型在编译期间不能够检测
   * 由此  使用 mapper接口类的使用方式 应运而生
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    // 在没有 xxxMapper 接口类的情况下 Foo.xml 中的 <mapper namespace="com.goat.test.none">  是可以随意指定的。
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo); //
  }

  @Test
  void test2() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    // 不加 namespace 前缀也可以调用 但是多个xml中一旦有重名的 selectById 将会冲突 所以最好加上 namespace前缀。
    Foo foo = sqlSession.selectOne("selectById",1);
    System.out.println(foo); //
  }
}
