
package org.apache.ibatis.submitted.camelcase;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CamelCaseMappingTest {

  protected static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/camelcase/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/camelcase/CreateDB.sql");

  }

  @Test
  void testList() {
    List<Camel> list = sqlSession.selectList("org.apache.ibatis.submitted.camel.doSelect");
    Assertions.assertTrue(list.size() > 0);
    Assertions.assertNotNull(list.get(0).getFirstName());
    Assertions.assertNull(list.get(0).getLAST_NAME());
  }

  @Test
  void testMap() {
    List<Map<String, Object>> list = sqlSession.selectList("org.apache.ibatis.submitted.camel.doSelectMap");
    Assertions.assertTrue(list.size() > 0);
    Assertions.assertTrue(list.get(0).containsKey("LAST_NAME"));
  }

}
