
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
import java.util.Map;

class DoNotCallSettersOnNullsTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/call_setters_on_nulls/mybatis-config-2.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/call_setters_on_nulls/CreateDB.sql");
  }

  @Test
  void shouldCallNullOnMappedProperty() {
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    User user = mapper.getUserMapped(1);
    Assertions.assertFalse(user.nullReceived);
  }

  @Test
  void shouldCallNullOnAutomaticMapping() {
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    User user = mapper.getUserUnmapped(1);
    Assertions.assertFalse(user.nullReceived);
  }

  @Test
  void shouldCallNullOnMap() {
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    Map user = mapper.getUserInMap(1);
    Assertions.assertFalse(user.containsKey("NAME"));
  }

}
