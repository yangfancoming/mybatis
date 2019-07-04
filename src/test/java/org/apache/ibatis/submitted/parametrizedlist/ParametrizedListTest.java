
package org.apache.ibatis.submitted.parametrizedlist;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.junit.jupiter.api.Assertions;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParametrizedListTest {

  private SqlSessionFactory sqlSessionFactory;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/parametrizedlist/Config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/parametrizedlist/CreateDB.sql");
  }

  @Test
  void testShouldDetectUsersAsParameterInsideAList() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User<String>> list = mapper.getAListOfUsers();
      Assertions.assertEquals(User.class, list.get(0).getClass());
    }
  }

  @Test
  void testShouldDetectUsersAsParameterInsideAMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      Map<Integer, User<String>> map = mapper.getAMapOfUsers();
      Assertions.assertEquals(User.class, map.get(1).getClass());
    }
  }

  @Test
  void testShouldGetAUserAsAMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      Map<String, Object> map = mapper.getUserAsAMap();
      Assertions.assertEquals(1, map.get("ID"));
    }
  }

  @Test
  void testShouldGetAListOfMaps() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Map<String, Object>> map = mapper.getAListOfMaps();
      Assertions.assertEquals(1, map.get(0).get("ID"));
    }
  }

}
