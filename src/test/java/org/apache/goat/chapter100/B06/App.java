package org.apache.goat.chapter100.B06;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;

import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/B06/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**  加入了 mapper接口类 以后 必须要有两处绑定：
   1. 文件绑定：xml 中的 <mapper namespace="org.apache.ibatis.zgoat.A02.FooMapper">  必须指定 接口类全路径
   1. 方法绑定：xml 中的 statement id  必须与  mapper接口类中的 方法名  一致！
   */

  @Test
  void findAll() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    List<Foo> all = fooMapper.findAll();
    System.out.println(all);
  }

  @Test
  void deleteById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    int i = fooMapper.deleteById(2);
    System.out.println(i);
  }

  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  /**
   *  mapper 接口 与 xml 文件绑定后   其实就是 xml 作为 mapper接口的实现类
   *  只不过这个实现类  是 由mybatis 创建的代理实现类  （JDK动态代理）
  */
  @Test
  void test2() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    System.out.println(fooMapper.getClass()); // class com.sun.proxy.$Proxy13
  }



}
