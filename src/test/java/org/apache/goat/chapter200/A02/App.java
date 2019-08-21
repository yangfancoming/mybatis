package org.apache.goat.chapter200.A02;

import org.apache.goat.common.Foo;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;


class App {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static FooMapper fooMapper;


  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/goat/chapter200/A02/mybatis-config.xml")) {
      // 通过 mybatis 全局配置文件  创建  sqlSessionFactory
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      // 通过 sqlSessionFactory 创建 sqlSession
      sqlSession = sqlSessionFactory.openSession(false);
      fooMapper= sqlSession.getMapper(FooMapper.class);
    }
    // 创建内存数据库 并添加插入测试数据
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/goat/chapter200/common/CreateDB.sql");
  }

  @AfterEach
  void after(){
    sqlSession.close();
  }


/**

 两处绑定：
 1. 文件绑定：xml 中的 <mapper namespace="org.apache.ibatis.zgoat.A02.FooMapper">  必须指定 接口类全路径
 1. 方法绑定：xml 中的 statement id  必须与  mapper接口类中的 方法名  一致！
*/
  @Test
  void test1() {
    List<Foo> all = fooMapper.findAll(); // 查库
    System.out.println(all);
    int i = fooMapper.deleteById(2);
    System.out.println(i);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }

  /**
   *  mapper 接口 与 xml 文件绑定后   其实就是 xml 作为 mapper接口的实现类
   *  只不过这个实现类  是 由mybatis 创建的代理实现类  （JDK动态代理）
  */
  @Test
  void test2() {
    System.out.println(fooMapper.getClass()); // class com.sun.proxy.$Proxy13
  }


  @Test
  void test3() {
    int i = fooMapper.deleteById(2);
    System.out.println(i);
  }

}
