
package org.apache.ibatis.submitted.complex_type;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class ComplexTypeTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/complex_type/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/complex_type/CreateDB.sql");
  }

  // see https://issues.apache.org/jira/browse/IBATIS-653
  @Test
  void shouldUpdateProps() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Item item = new Item();
      item.id = 10;
      Property p1 = new Property();
      p1.id = 11;
      p1.value = "value11";
      Property p2 = new Property();
      p2.id = 12;
      p2.value = "value12";
      List<Property> list = new ArrayList<>();
      list.add(p1);
      list.add(p2);
      item.properties = list;
      sqlSession.update("updateProps", item);
    }
  }

}
