
package org.apache.ibatis.submitted.extend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExtendTest {

  protected static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/extend/ExtendConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/extend/CreateDB.sql");
  }

  @Test
  void testExtend() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      ExtendMapper mapper = sqlSession.getMapper(ExtendMapper.class);
      Child answer = mapper.selectChild();
      assertEquals(answer.getMyProperty(), "last");
    }
  }

}
