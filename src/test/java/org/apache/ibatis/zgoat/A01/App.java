
package org.apache.ibatis.zgoat.A01;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;


class App {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/zgoat/A01/mybatis-config.xml")) {
      // 通过 mybatis 全局配置文件  创建  sqlSessionFactory
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      // 通过 sqlSessionFactory 创建 sqlSession
      sqlSession = sqlSessionFactory.openSession(false);
    }
    // 创建内存数据库 并添加插入测试数据
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/zgoat/A01/CreateDB.sql");
  }

  @AfterEach
  void after(){
    sqlSession.close();
  }

/**
 *  查询后 会发现  lastname=null 的问题 是因为 实体类的 lastname 属性 与 数据库表的 last_name 没有对应上
 *  解决方法：
 *  1. 改sql    select id,firstname,last_name as lastname from foo where id = #{id}
 *  2. 全局 mybatis-config.xml <configuration> <settings>  增加  <setting name="mapUnderscoreToCamelCase" value="true"/>
*/
  @Test
  void test() {
    Foo foo = sqlSession.selectOne("org.apache.ibatis.zgoat.A01.FooMapper.selectById",2);
    System.out.println(foo); // id=2, lastname=null, firstname=222
  }




  @Test
  void test1() {
    FooMapper fooMapper= sqlSession.getMapper(FooMapper.class);
    List<Foo> all = fooMapper.findAll(); // 查库
    System.out.println(all);
  }



}
