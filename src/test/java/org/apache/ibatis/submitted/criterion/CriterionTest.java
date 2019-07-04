
package org.apache.ibatis.submitted.criterion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CriterionTest {

  protected static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/criterion/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/criterion/CreateDB.sql");
  }

  @Test
  void testSimpleSelect() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Criterion criterion = new Criterion();
      criterion.setTest("firstName =");
      criterion.setValue("Fred");
      Parameter parameter = new Parameter();
      parameter.setCriterion(criterion);

      List<Map<String, Object>> answer =
          sqlSession.selectList("org.apache.ibatis.submitted.criterion.simpleSelect", parameter);

      assertEquals(1, answer.size());
    }
  }
}
