
package org.apache.ibatis.submitted.language;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Just a test case. Not a real Velocity implementation.
 */
public class LanguageTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/language/MapperConfig.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/language/CreateDB.sql";

  @BeforeEach
  public void beforeAll() throws Exception {
    setUpByInputStream(XMLPATH,DBSQL);
  }

  @Test
  public void testDynamicSelectWithPropertyParams() {
    // SELECT firstName , lastName FROM names WHERE lastName LIKE ?
    Parameter p = new Parameter(true, "Fli%");
    List<Name> answer = sqlSession.selectList("selectNames", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }

    p = new Parameter(false, "Fli%");
    answer = sqlSession.selectList("selectNames", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertNull(n.getLastName());
    }

    p = new Parameter(false, "Rub%");
    answer = sqlSession.selectList("selectNames", p);
    assertEquals(2, answer.size());
    for (Name n : answer) {
      assertNull(n.getLastName());
    }
  }

  @Test
  public void testDynamicSelectWithExpressionParams() {
    Parameter p = new Parameter(true, "Fli");
    List<Name> answer = sqlSession.selectList("selectNamesWithExpressions", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }

    p = new Parameter(false, "Fli");
    answer = sqlSession.selectList("selectNamesWithExpressions", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertNull(n.getLastName());
    }

    p = new Parameter(false, "Rub");
    answer = sqlSession.selectList("selectNamesWithExpressions", p);
    assertEquals(2, answer.size());
    for (Name n : answer) {
      assertNull(n.getLastName());
    }
  }

  @Test
  public void testDynamicSelectWithIteration() {
    int[] ids = { 2, 4, 5 };
    Map<String, Object> param = new HashMap<>();
    param.put("ids", ids);
    List<Name> answer = sqlSession.selectList("selectNamesWithIteration", param);
    assertEquals(3, answer.size());
    for (int i = 0; i < ids.length; i++) {
      assertEquals(ids[i], answer.get(i).getId());
    }
  }

  @Test
  void testLangRaw() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Parameter p = new Parameter(true, "Fli%");
      List<Name> answer = sqlSession.selectList("selectRaw", p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals("Flintstone", n.getLastName());
      }
    }
  }

  @Test
  public void testLangRawWithInclude() {
    Parameter p = new Parameter(true, "Fli%");
    List<Name> answer = sqlSession.selectList("selectRawWithInclude", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

  @Test
  public void testLangRawWithIncludeAndCData() {
    Parameter p = new Parameter(true, "Fli%");
    List<Name> answer = sqlSession.selectList("selectRawWithIncludeAndCData", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

  @Test
  public void testLangXmlTags() {
    Parameter p = new Parameter(true, "Fli%");
    List<Name> answer = sqlSession.selectList("selectXml", p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

  @Test
  public void testLangRawWithMapper() {
    Parameter p = new Parameter(true, "Fli%");
    Mapper m = sqlSession.getMapper(Mapper.class);
    List<Name> answer = m.selectRawWithMapper(p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

  @Test
  void testLangVelocityWithMapper() {
    Parameter p = new Parameter(true, "Fli%");
    Mapper m = sqlSession.getMapper(Mapper.class);
    List<Name> answer = m.selectVelocityWithMapper(p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

  @Test
  void testLangXmlWithMapper() {
    Parameter p = new Parameter(true, "Fli%");
    Mapper m = sqlSession.getMapper(Mapper.class);
    List<Name> answer = m.selectXmlWithMapper(p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

  @Test
  void testLangXmlWithMapperAndSqlSymbols() {
    Parameter p = new Parameter(true, "Fli%");
    Mapper m = sqlSession.getMapper(Mapper.class);
    List<Name> answer = m.selectXmlWithMapperAndSqlSymbols(p);
    assertEquals(3, answer.size());
    for (Name n : answer) {
      assertEquals("Flintstone", n.getLastName());
    }
  }

}
