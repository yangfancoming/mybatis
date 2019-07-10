
package org.apache.ibatis.submitted.associationtype;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.junit.jupiter.api.Assertions;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AssociationTypeTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {

    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/associationtype/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/associationtype/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      List<Map> results = sqlSession.selectList("getUser");
      for (Map r : results) {
        Assertions.assertEquals(String.class, r.get("a1").getClass());
        Assertions.assertEquals(String.class, r.get("a2").getClass());
      }
    }
  }

}
