
package org.apache.ibatis.zgoat.A01;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;


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
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/ibatis/zgoat/common/CreateDB.sql");
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
    // 在没有 xxxMapper 接口类的情况下 Foo.xml 中的 <mapper namespace="com.goat.test.none">  是可以随意指定的。
    Foo foo1 = sqlSession.selectOne("com.goat.test.none.selectById",2);
    System.out.println(foo1); // id=2, lastname=null, firstname=222

    // 不加 namespace 前缀也可以调用 但是多个xml中一旦有重名的 selectById 将会冲突 所以最好加上 namespace前缀。
    Foo foo2 = sqlSession.selectOne("selectById",2);
    System.out.println(foo2 == foo1); // 走缓存
  }



}
