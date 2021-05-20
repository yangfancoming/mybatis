
package org.apache.ibatis.submitted.hashmaptypehandler;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.HashMap;

class HashMapTypeHandlerTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/hashmaptypehandler/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/hashmaptypehandler/CreateDB.sql");
  }

  @Test
  void shouldNotApplyTypeHandlerToParamMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUser(1, "User1");
      Assertions.assertEquals("User1", user.getName());
    }
  }

  @Test
  void shouldNotApplyTypeHandlerToParamMapXml() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserXml(1, "User1");
      Assertions.assertEquals("User1", user.getName());
    }
  }

  @Test
  void shouldApplyHashMapTypeHandler() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      HashMap<String, String> map = new HashMap<>();
      map.put("name", "User1");
      User user = mapper.getUserWithTypeHandler(map);
      Assertions.assertNotNull(user);
    }
  }

  @Test
  void shouldApplyHashMapTypeHandlerXml() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      HashMap<String, String> map = new HashMap<>();
      map.put("name", "User1");
      User user = mapper.getUserWithTypeHandlerXml(map);
      Assertions.assertNotNull(user);
    }
  }
}
