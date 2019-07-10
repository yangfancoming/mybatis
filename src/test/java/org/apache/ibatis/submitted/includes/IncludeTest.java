
package org.apache.ibatis.submitted.includes;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;

class IncludeTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {

    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/includes/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/includes/CreateDB.sql");
  }

  @Test
  void testIncludes()  {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final Integer result = sqlSession.selectOne("org.apache.ibatis.submitted.includes.mapper.selectWithProperty");
      Assertions.assertEquals(Integer.valueOf(1), result);
    }
  }

  @Test
  void testParametrizedIncludes() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final Map<String, Object> result = sqlSession.selectOne("org.apache.ibatis.submitted.includes.mapper.select");
      // Assertions.assertEquals(Integer.valueOf(1), result);
    }
  }

}
