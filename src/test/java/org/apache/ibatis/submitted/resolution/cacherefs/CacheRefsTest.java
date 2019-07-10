
package org.apache.ibatis.submitted.resolution.cacherefs;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.resolution.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CacheRefsTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources
      .getResourceAsReader("org/apache/ibatis/submitted/resolution/cacherefs/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
      "org/apache/ibatis/submitted/resolution/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MapperB mapper = sqlSession.getMapper(MapperB.class);
      User user = mapper.getUser(1);
      Assertions.assertEquals(Integer.valueOf(1), user.getId());
      Assertions.assertEquals("User1", user.getName());
    }
  }

}
