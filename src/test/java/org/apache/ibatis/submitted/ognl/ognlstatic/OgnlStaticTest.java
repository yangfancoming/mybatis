
package org.apache.ibatis.submitted.ognl.ognlstatic;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

class OgnlStaticTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/ognl/ognlstatic/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/ibatis/submitted/ognl/ognlstatic/CreateDB.sql");
  }

  /**
   * This is the log output.
   * DEBUG [main] - ooo Using Connection [org.hsqldb.jdbc.JDBCConnection@5ae1a5c7]
   * DEBUG [main] - ==>  Preparing: SELECT * FROM users WHERE name IN (?) AND id = ?
   * DEBUG [main] - ==> Parameters: 1(Integer), 1(Integer)
   * There are two parameter mappings but DefaulParameterHandler maps them both to input paremeter (integer)
   */
  @Test // see issue #448
  void shouldGetAUserStatic() {
      User user = mapper.getUserStatic(1);
      Assertions.assertNotNull(user);
      Assertions.assertEquals("User1", user.getName());
  }

  @Test // see issue #61 (gh)
  void shouldGetAUserWithIfNode() {
      User user = mapper.getUserIfNode("User1");
      Assertions.assertEquals("User1", user.getName());
  }

}
