
package org.apache.ibatis.submitted.column_forwarding;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

class ColumnForwardingTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/column_forwarding/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);

    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/column_forwarding/CreateDB.sql");
  }

  @Test
  void shouldGetUserWithGroup() {
      User user = mapper.getUser(1);
      Assertions.assertNotNull(user);
      Assertions.assertNotNull(user.getId());
      Assertions.assertEquals("active", user.getState());
      Assertions.assertNotNull(user.getGroup());
      Assertions.assertNotNull(user.getGroup().getId());
      Assertions.assertEquals("active", user.getGroup().getState());
  }

  @Test
  void shouldGetUserWithoutGroup() {
      User user = mapper.getUser(2);
      Assertions.assertNotNull(user);
      Assertions.assertNotNull(user.getId());
      Assertions.assertNull(user.getState());
      Assertions.assertNull(user.getGroup());
  }
}
