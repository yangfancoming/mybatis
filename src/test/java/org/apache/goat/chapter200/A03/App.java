package org.apache.goat.chapter200.A03;


import org.apache.goat.common.model.Bar;
import org.apache.goat.common.model.Foo;
import org.apache.goat.common.model.Zoo;
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
  private static FooMapper fooMapper;
  private static BarMapper barMapper;
  private static ZooMapper zooMapper;

  @BeforeEach
  void setUp() throws Exception {
    /**   如果是在某个包下  则： "org/apache/ibatis/zgoat/A02/mybatis-config.xml"  */
    String path = "mybatis-config.xml"; // 实际加载的是类路径下的 resources/mybatis-config.xml
    try (Reader reader = Resources.getResourceAsReader(path)) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession(false);
      barMapper= sqlSession.getMapper(BarMapper.class);
      fooMapper= sqlSession.getMapper(FooMapper.class);
      zooMapper= sqlSession.getMapper(ZooMapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/goat/chapter200/common/CreateDB.sql");
  }

  @AfterEach
  void after(){
    sqlSession.close();
  }



  /**      select * from foo where id = #{id}
   [2019-08-02 22:07:56,969]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:134)DEBUG:==>  Preparing: select * from foo where id = ?
   [2019-08-02 22:07:56,996]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:134)DEBUG:==> Parameters: 1(Integer)
   [2019-08-02 22:07:57,024]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:134)DEBUG:<==      Total: 1
  */
  @Test
  void test2() {
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  /**      select * from foo where id = ${id}
   [2019-08-02 22:07:34,187]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:134)DEBUG:==>  Preparing: select * from foo where id = 1
   [2019-08-02 22:07:34,214]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:134)DEBUG:==> Parameters:
   [2019-08-02 22:07:34,240]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:134)DEBUG:<==      Total: 1
  */
  @Test
  void test4() {
    Foo foo = fooMapper.selectById1(1);
    System.out.println(foo);
  }

  @Test
  void test3() {
    Bar bar = barMapper.selectById(1);
    System.out.println(bar);
  }

  @Test
  void test5() {
//    Bar bar = zooMapper.selectById(1,"11");
    Zoo zoo = zooMapper.selectById(1);
    System.out.println(zoo);
  }
}
