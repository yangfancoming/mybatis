
package org.apache.ibatis.submitted.array_result_type;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayResultTypeTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/array_result_type/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
       mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/array_result_type/CreateDB.sql");
  }

  @Test
  void shouldGetUserArray() {
    User[] users = mapper.getUsers();
    assertEquals("User1", users[0].getName());
    assertEquals("User2", users[1].getName());
  }

  @Test
  void shouldGetUserArrayXml() {
    User[] users = mapper.getUsersXml();
    assertEquals("User1", users[0].getName());
    assertEquals("User2", users[1].getName());
  }

  @Test
  void shouldGetSimpleTypeArray() {
    Integer[] ids = mapper.getUserIds();
    assertEquals(Integer.valueOf(1), ids[0]);
  }

  @Test
  void shouldGetPrimitiveArray() {
    int[] ids = mapper.getUserIdsPrimitive();
    assertEquals(1, ids[0]);
  }
}
