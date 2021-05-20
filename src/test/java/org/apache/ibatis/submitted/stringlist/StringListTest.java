
package org.apache.ibatis.submitted.stringlist;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class StringListTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/stringlist/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/stringlist/CreateDB.sql");
  }

  @Test
  void shouldMapListOfStrings() {
      List<User> users = mapper.getUsersAndGroups(1);
      Assertions.assertEquals(1, users.size());
      Assertions.assertEquals(2, users.get(0).getGroups().size());
      Assertions.assertEquals(2, users.get(0).getRoles().size());
  }

  @Test
  void shouldMapListOfStringsToMap() {
      List<Map<String, Object>> results = mapper.getUsersAndGroupsMap(1);
      Assertions.assertEquals(1, results.size());
      Assertions.assertEquals(2, ((List<?>)results.get(0).get("groups")).size());
      Assertions.assertEquals(2, ((List<?>)results.get(0).get("roles")).size());
  }

  @Test
  void shouldFailFastIfCollectionTypeIsAmbiguous() throws Exception {
    try (Reader reader = Resources .getResourceAsReader("org/apache/ibatis/submitted/stringlist/mybatis-config-invalid.xml")) {
      new SqlSessionFactoryBuilder().build(reader);
      fail("Should throw exception when collection type is unresolvable.");
    } catch (PersistenceException e) {
      assertTrue(e.getMessage().contains("Ambiguous collection type for property 'groups'. You must specify 'javaType' or 'resultMap'."));
    }
  }
}
