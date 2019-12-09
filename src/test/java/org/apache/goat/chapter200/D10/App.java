package org.apache.goat.chapter200.D10;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;

/**
 * ParameterHandler
 * ResultSetHandler
 * StatementHandler
 *
 *
 * 插件编写：
 * 1、编写Interceptor的实现类
 * 2、使用@Intercepts注解完成插件签名
 * 3、将写好的插件注册到全局配置文件中
*/

public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter200/D10/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
//    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.selectById",2);
//    System.out.println(foo1);
  }

}
