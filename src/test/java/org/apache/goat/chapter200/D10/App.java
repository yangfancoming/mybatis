package org.apache.goat.chapter200.D10;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;

/**
 * 插件编写：
 * 1、编写Interceptor的实现类
 * 2、使用@Intercepts注解完成插件签名
 * 3、将写好的插件注册到全局配置文件中
 * 创建动态代理的时候，是按照插件配置顺序创建层层代理对象，
 * 在执行目标方法的时候，是按照逆向顺序执行
*/

public class App extends MyBaseDataTest {

  public static final String CONFIG1 = "org/apache/goat/chapter200/D10/config1.xml";
  public static final String CONFIG2 = "org/apache/goat/chapter200/D10/config2.xml";
  public static final String CONFIG3 = "org/apache/goat/chapter200/D10/config3.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  @Test
  void test1() throws Exception {
    setUpByReader(CONFIG1,DBSQL);
    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.selectById",2);
    System.out.println(foo1);
  }

  @Test
  void test2() throws Exception {
    setUpByReader(CONFIG2,DBSQL);
    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.selectById",2);
    System.out.println(foo1);
  }

  @Test
  void test3() throws Exception {
    setUpByReader(CONFIG3,DBSQL);
    Foo foo1 = sqlSession.selectOne("com.goat.test.namespace.selectById",2);
    System.out.println(foo1);
  }
}
