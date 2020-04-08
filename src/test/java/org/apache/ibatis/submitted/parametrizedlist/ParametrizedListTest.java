
package org.apache.ibatis.submitted.parametrizedlist;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ParametrizedListTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/parametrizedlist/Config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/parametrizedlist/CreateDB.sql";

  Mapper mapper;

  @BeforeEach
  void setUp() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    mapper = sqlSession.getMapper(Mapper.class);
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
