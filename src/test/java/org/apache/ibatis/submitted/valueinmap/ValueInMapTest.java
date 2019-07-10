
package org.apache.ibatis.submitted.valueinmap;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValueInMapTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {

    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/valueinmap/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/valueinmap/CreateDB.sql");
  }

  @Test // issue #165
  void shouldWorkWithAPropertyNamedValue() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Map<String, String> map = new HashMap<>();
      map.put("table", "users");
      map.put("column", "name");
      map.put("value", "User1");
      Integer count = sqlSession.selectOne("count", map);
      Assertions.assertEquals(Integer.valueOf(1), count);
    }
  }

  @Test
  void shouldWorkWithAList() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      List<String> list = new ArrayList<>();
      list.add("users");
      Assertions.assertThrows(PersistenceException.class, () -> sqlSession.selectOne("count2",list));
    }
  }

}
