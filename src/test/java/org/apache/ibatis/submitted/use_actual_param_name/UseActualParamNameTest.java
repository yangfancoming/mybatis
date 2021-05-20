
package org.apache.ibatis.submitted.use_actual_param_name;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseActualParamNameTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader(
        "org/apache/ibatis/submitted/use_actual_param_name/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
        "org/apache/ibatis/submitted/use_actual_param_name/CreateDB.sql");
  }

  @Test
  void shouldSingleParamBeReferencedByAnyName() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserById(1);
      assertNotNull(user);
    }
  }

  @Test
  void shouldMultipleParamsBeReferencedByActualNames() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserByIdAndName(1, "User1");
      assertNotNull(user);
    }
  }

  @Test
  void shouldSoleListParamBeReferencedByImplicitName() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getUsersByIdList(Arrays.asList(1, 2));
      assertEquals(2, users.size());
    }
  }

  @Test
  void shouldListParamBeReferencedByActualNameIfAnotherParamExists() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getUsersByIdListAndName(Arrays.asList(1, 2), null);
      assertEquals(2, users.size());
    }
  }

}
