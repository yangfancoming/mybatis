package org.apache.goat.chapter200.A01;

import org.apache.goat.common.model.Foo;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;

/**  小结：
 * 1、接口式编程
 * 	原生：		Dao		====>  DaoImpl
 * 	mybatis：	Mapper	====>  xxMapper.xml
 *
 * 2、SqlSession代表和数据库的一次会话；用完必须关闭；
 * 3、SqlSession和connection 一样它们都是非线程安全。每次使用都应该去获取新的对象。
 * 4、mapper接口没有实现类，但是mybatis会为这个接口生成一个代理对象。（将接口和xml进行绑定）
 * 		EmployeeMapper empMapper =	sqlSession.getMapper(EmployeeMapper.class);
 * 5、两个重要的配置文件：
 * 		mybatis的全局配置文件：包含数据库连接池信息，事务管理器信息等...系统运行环境信息
 * 		sql 映射文件：保存了每一个sql语句的映射信息：将sql抽取出来。
 */

class App {

  private static SqlSessionFactory sqlSessionFactory;
  // 由于 SqlSession 和 connection 一样都是非现场安全的  因此不能当做成员变量 此处只是用作 学习 测试之用
  private static SqlSession sqlSession;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/goat/chapter200/A01/mybatis-config.xml")) {
      // 通过 mybatis 全局配置文件  创建  sqlSessionFactory
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      // 通过 sqlSessionFactory 创建 sqlSession
      sqlSession = sqlSessionFactory.openSession(false);
    }
    // 创建内存数据库 并添加插入测试数据
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/goat/common/CreateDB.sql");
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
