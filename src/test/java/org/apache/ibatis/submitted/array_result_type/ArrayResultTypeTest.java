
package org.apache.ibatis.submitted.array_result_type;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ArrayResultTypeTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/array_result_type/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/array_result_type/CreateDB.sql");
  }

  @Test
  void shouldGetUserArray() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User[] users = mapper.getUsers();
      assertEquals("User1", users[0].getName());
      assertEquals("User2", users[1].getName());
    }
  }

  @Test
  void shouldGetUserArrayXml() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User[] users = mapper.getUsersXml();
      assertEquals("User1", users[0].getName());
      assertEquals("User2", users[1].getName());
    }
  }

  @Test
  void shouldGetSimpleTypeArray() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      Integer[] ids = mapper.getUserIds();
      assertEquals(Integer.valueOf(1), ids[0]);
    }
  }

  @Test
  void shouldGetPrimitiveArray() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      int[] ids = mapper.getUserIdsPrimitive();
      assertEquals(1, ids[0]);
    }
  }
}
