
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
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/call_setters_on_nulls/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/call_setters_on_nulls/CreateDB.sql");
  }

  @Test
  void shouldCallNullOnMappedProperty() {
    User user = mapper.getUserMapped(1);
    Assertions.assertTrue(user.nullReceived);
  }

  @Test
  void shouldCallNullOnAutomaticMapping() {
    User user = mapper.getUserUnmapped(1);
    Assertions.assertTrue(user.nullReceived);
  }

  @Test
  void shouldCallNullOnMap() {
    Map user = mapper.getUserInMap(1);
    Assertions.assertTrue(user.containsKey("NAME"));
  }

  @Test
  void shouldCallNullOnMapForSingleColumn() {
    List<Map<String, Object>> oneColumns = mapper.getNameOnly();
    // When callSetterOnNull is true, setters are called with null values
    // but if all the values for an object are null
    // the object itself should be null (same as default behaviour)
    Assertions.assertNull(oneColumns.get(1));
  }

  @Test
  void shouldCallNullOnMapForSingleColumnWithResultMap() {
    List<Map<String, Object>> oneColumns = mapper.getNameOnlyMapped();
    // Assertions.assertNotNull(oneColumns.get(1));
    // TEST changed after fix for #307
    // When callSetterOnNull is true, setters are called with null values
    // but if all the values for an object are null
    // the object itself should be null (same as default behaviour)
    Assertions.assertNull(oneColumns.get(1));
  }

}
