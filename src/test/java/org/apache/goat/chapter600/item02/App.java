package org.apache.goat.chapter600.item02;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;


/**
 # mybatis  读取 xml 的三种方式
 * SqlSessionFactoryBuilder 类有很多的构造方法，但主要分为三大类：
 * 1、通过读取字符流（Reader）的方式构件SqlSessionFactory。
 * 2、通过字节流（InputStream）的方式构件SqlSessionFacotry。
 * 3、通过Configuration对象构建SqlSessionFactory。
 * 第1、2种方式是通过配置文件方式，第3种是通过Java代码方式。
*/
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter600/item02/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.findList",2);
//    System.out.println(foo1); //
  }



}
