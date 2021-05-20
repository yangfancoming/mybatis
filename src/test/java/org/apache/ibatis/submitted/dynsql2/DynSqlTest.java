
package org.apache.ibatis.submitted.dynsql2;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynSqlTest {

  protected static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/dynsql2/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/dynsql2/CreateDB.sql");
  }

  @Test
  void testDynamicSelectWithTypeHandler() {
      List<Name> names = new ArrayList<>();
      Name name = new Name();
      name.setFirstName("Fred");
      name.setLastName("Flintstone");
      names.add(name);

      name = new Name();
      name.setFirstName("Barney");
      name.setLastName("Rubble");
      names.add(name);

      Parameter parameter = new Parameter();
      parameter.setNames(names);

      List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql2.dynamicSelectWithTypeHandler", parameter);
      assertEquals(2, answer.size());
  }

  @Test
  void testSimpleSelect() {
      Map<String, Object> answer = sqlSession.selectOne("org.apache.ibatis.submitted.dynsql2.simpleSelect", 1);
      assertEquals(answer.get("ID"), 1);
      assertEquals(answer.get("FIRSTNAME"), "Fred");
      assertEquals(answer.get("LASTNAME"), "Flintstone");
  }
}
