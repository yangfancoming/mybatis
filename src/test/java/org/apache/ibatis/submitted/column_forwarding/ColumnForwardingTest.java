
package org.apache.ibatis.submitted.column_forwarding;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ColumnForwardingTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/column_forwarding/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/column_forwarding/CreateDB.sql");
  }

  @Test
  void shouldGetUserWithGroup() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUser(1);
      Assertions.assertNotNull(user);
      Assertions.assertNotNull(user.getId());
      Assertions.assertEquals("active", user.getState());
      Assertions.assertNotNull(user.getGroup());
      Assertions.assertNotNull(user.getGroup().getId());
      Assertions.assertEquals("active", user.getGroup().getState());
    }
  }

  @Test
  void shouldGetUserWithoutGroup() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUser(2);
      Assertions.assertNotNull(user);
      Assertions.assertNotNull(user.getId());
      Assertions.assertNull(user.getState());
      Assertions.assertNull(user.getGroup());
    }
  }
}
