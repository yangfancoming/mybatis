
package org.apache.ibatis.submitted.call_setters_on_nulls;

import org.apache.ibatis.BaseDataTest;
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

class CallSettersOnNullsTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create a SqlSessionFactory
    try (Reader reader = Resources
        .getResourceAsReader("org/apache/ibatis/submitted/call_setters_on_nulls/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/call_setters_on_nulls/CreateDB.sql");
  }

  @Test
  void shouldCallNullOnMappedProperty() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserMapped(1);
      Assertions.assertTrue(user.nullReceived);
    }
  }

  @Test
  void shouldCallNullOnAutomaticMapping() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserUnmapped(1);
      Assertions.assertTrue(user.nullReceived);
    }
  }

  @Test
  void shouldCallNullOnMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      Map user = mapper.getUserInMap(1);
      Assertions.assertTrue(user.containsKey("NAME"));
    }
  }

  @Test
  void shouldCallNullOnMapForSingleColumn() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Map<String, Object>> oneColumns = mapper.getNameOnly();
      // When callSetterOnNull is true, setters are called with null values
      // but if all the values for an object are null
      // the object itself should be null (same as default behaviour)
      Assertions.assertNull(oneColumns.get(1));
    }
  }

  @Test
  void shouldCallNullOnMapForSingleColumnWithResultMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Map<String, Object>> oneColumns = mapper.getNameOnlyMapped();
      // Assertions.assertNotNull(oneColumns.get(1));
      // TEST changed after fix for #307
      // When callSetterOnNull is true, setters are called with null values
      // but if all the values for an object are null
      // the object itself should be null (same as default behaviour)
      Assertions.assertNull(oneColumns.get(1));
    }
  }

}
