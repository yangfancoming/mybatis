
package org.apache.ibatis.submitted.duplicate_statements;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DuplicateStatementsTest {

  private SqlSessionFactory sqlSessionFactory;

  @BeforeEach
  void setupDb() throws Exception {
      // create a SqlSessionFactory
      try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/duplicate_statements/mybatis-config.xml")) {
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      }

      // populate in-memory database
      BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
              "org/apache/ibatis/submitted/duplicate_statements/CreateDB.sql");
  }

  @Test
  void shouldGetAllUsers() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getAllUsers();
      Assertions.assertEquals(10, users.size());
    }
  }

  @Test
  void shouldGetFirstFourUsers() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getAllUsers(new RowBounds(0, 4));
      Assertions.assertEquals(4, users.size());
    }
  }

  @Test
  @Disabled("fails currently - issue 507")
  void shouldGetAllUsers_Annotated() {
    sqlSessionFactory.getConfiguration().addMapper(AnnotatedMapper.class);
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      AnnotatedMapper mapper = sqlSession.getMapper(AnnotatedMapper.class);
      List<User> users = mapper.getAllUsers();
      Assertions.assertEquals(10, users.size());
    }
  }

  @Test
  @Disabled("fails currently - issue 507")
  void shouldGetFirstFourUsers_Annotated() {
    sqlSessionFactory.getConfiguration().addMapper(AnnotatedMapper.class);
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      AnnotatedMapper mapper = sqlSession.getMapper(AnnotatedMapper.class);
      List<User> users = mapper.getAllUsers(new RowBounds(0, 4));
      Assertions.assertEquals(4, users.size());
    }
  }

  @Test
  void shouldFailForDuplicateMethod() {
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> sqlSessionFactory.getConfiguration().addMapper(AnnotatedMapperExtended.class));
  }
}
