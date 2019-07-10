
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
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/parametrizedlist/Config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/parametrizedlist/CreateDB.sql");
  }

  @Test
  void testShouldDetectUsersAsParameterInsideAList() {
    List<User> list = mapper.getAListOfUsers();
    Assertions.assertEquals(User.class, list.get(0).getClass());
  }

  @Test
  void testShouldDetectUsersAsParameterInsideAMap() {
    Map<Integer, User> map = mapper.getAMapOfUsers();
    Assertions.assertEquals(User.class, map.get(1).getClass());
  }

  @Test
  void testShouldGetAUserAsAMap() {
    Map<String, Object> map = mapper.getUserAsAMap();
    Assertions.assertEquals(1, map.get("ID"));
  }

  @Test
  void testShouldGetAListOfMaps() {
    List<Map<String, Object>> map = mapper.getAListOfMaps();
    Assertions.assertEquals(1, map.get(0).get("ID"));
  }

}
