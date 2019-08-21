package org.apache.goat.chapter100.A00;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.chapter200.common.Foo;
import org.junit.jupiter.api.Test;



class App extends MyBaseDataTest {


  /**  1、通过读取字符流（Reader）的方式构件SqlSessionFactory */
  @Test
  void Reader() throws Exception {
    setUpByReader("org/apache/goat/chapter100/A00/mybatis-config.xml","org/apache/goat/chapter200/common/CreateDB.sql");
    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.selectById",2);
    System.out.println(foo1); //
  }


  /**  2、通过字节流（InputStream）的方式构件SqlSessionFacotry */
  @Test
  void InputStream() throws Exception {
    setUpByInputStream("org/apache/goat/chapter100/A00/mybatis-config.xml","org/apache/goat/chapter200/common/CreateDB.sql");
    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo1); //
  }


  /**  3、通过Configuration对象构建SqlSessionFactory */

}
