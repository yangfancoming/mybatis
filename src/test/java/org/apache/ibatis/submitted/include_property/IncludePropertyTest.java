
package org.apache.ibatis.submitted.include_property;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncludePropertyTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/include_property/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/include_property/CreateDB.sql";

  @BeforeEach
  public void beforeAll() throws Exception {
    setUpByInputStream(XMLPATH,DBSQL);
  }

  @Test
  void testSimpleProperty() {
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectSimpleA");
    assertEquals("col_a value", results.get(0));
    results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectSimpleB");
    assertEquals("col_b value", results.get(0));
  }

  @Test
  void testPropertyContext() {
    List<Map<String, String>> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectPropertyContext");
    Map<String, String> map = results.get(0);
    assertEquals(2, map.size());
    assertEquals("col_a value", map.get("COL_A"));
    assertEquals("col_b value", map.get("COL_B"));
  }

  @Test
  void testNestedDynamicValue() {
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectNestedDynamicValue");
    assertEquals("col_a value", results.get(0));
  }

  @Test
  void testEmptyString() {
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectEmptyProperty");
    assertEquals("a value", results.get(0));
  }

  @Test
  void testPropertyInRefid() {
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectPropertyInRefid");
    assertEquals("col_a value", results.get(0));
  }

  @Test
  void testConfigVar() {
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectConfigVar");
    assertEquals("col_c value", results.get(0), "Property defined in the config file should be used.");
  }

  @Test
  void testRuntimeVar() {
    Map<String, String> params = new HashMap<>();
    params.put("suffix", "b");
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectRuntimeVar", params);
    assertEquals("col_b value", results.get(0));
  }

  @Test
  void testNestedInclude() {
    List<String> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectNestedInclude");
    assertEquals("a value", results.get(0));
  }

  @Test
  void testParametersInAttribute() {
    List<Map<String, String>> results = sqlSession.selectList("org.apache.ibatis.submitted.include_property.Mapper.selectPropertyInAttribute");
    Map<String, String> map = results.get(0);
    assertEquals(2, map.size());
    assertEquals("col_a value", map.get("COL_1"));
    assertEquals("col_b value", map.get("COL_2"));
  }
}
