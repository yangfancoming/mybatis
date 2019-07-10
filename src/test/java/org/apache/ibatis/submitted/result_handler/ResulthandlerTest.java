
package org.apache.ibatis.submitted.result_handler;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ResulthandlerTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {

    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/result_handler/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSessionFactory.getConfiguration().addMapper(Mapper.class);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/result_handler/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUser(1);
      Assertions.assertEquals("User1", user.getName());
    }
  }

  @Test
  void shouldGetAllUsers() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      UserResultHandler userResultHandler = new UserResultHandler();
      mapper.getAllUsers(userResultHandler);
      Assertions.assertEquals(3, userResultHandler.getUsers().size());
    }
  }
}
