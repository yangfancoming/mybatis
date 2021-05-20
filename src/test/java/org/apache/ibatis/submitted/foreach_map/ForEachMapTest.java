
package org.apache.ibatis.submitted.foreach_map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;

import java.io.Reader;
import java.util.List;

class ForEachMapTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUpClass() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/foreach_map/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/foreach_map/CreateDB.sql");
  }

  @BeforeEach
  void setUp() {
    sqlSession = sqlSessionFactory.openSession();
  }

  @AfterEach
  void tearDown() {
    sqlSession.close();
  }

  @Test
  void shouldGetStringKeyStringValueEntries() {
    MapParam mapParam = new MapParam();
    mapParam.getMap().put("key 1", "value 1");
    mapParam.getMap().put("key 2", "value 2");
    sqlSession.insert("ins_string_string", mapParam);

    List<StringStringMapEntry> entries = sqlSession.selectList("sel_string_string", new MapParam());
    Assertions.assertEquals(new StringStringMapEntry("key 1", "value 1"), entries.get(0));
    Assertions.assertEquals(new StringStringMapEntry("key 2", "value 2"), entries.get(1));
  }

  @Test
  void shouldGetIntKeyBoolValueEntries() {
    MapParam mapParam = new MapParam();
    mapParam.getMap().put(12345, true);
    mapParam.getMap().put(54321, false);
    sqlSession.insert("ins_int_bool", mapParam);

    List<IntBoolMapEntry> entries = sqlSession.selectList("sel_int_bool");
    Assertions.assertEquals(new IntBoolMapEntry(12345, true), entries.get(0));
    Assertions.assertEquals(new IntBoolMapEntry(54321, false), entries.get(1));
  }

  @Test
  void shouldGetNestedBeanKeyValueEntries() {
    MapParam mapParam = new MapParam();
    mapParam.getMap().put(new NestedBean(12345, true), new NestedBean(54321, false));
    mapParam.getMap().put(new NestedBean(67890, true), new NestedBean(9876, false));
    sqlSession.insert("ins_nested_bean", mapParam);

    List<NestedBeanMapEntry> entries = sqlSession.selectList("sel_nested_bean");
    Assertions.assertEquals(new NestedBeanMapEntry(12345, true, 54321, false), entries.get(0));
    Assertions.assertEquals(new NestedBeanMapEntry(67890, true, 9876, false), entries.get(1));
  }

  @Test
  void shouldSubstituteIndexWithKey() {
    MapParam mapParam = new MapParam();
    mapParam.getMap().put("col_a", 22);
    mapParam.getMap().put("col_b", 222);
    Integer count = sqlSession.selectOne("sel_key_cols", mapParam);
    Assertions.assertEquals(Integer.valueOf(1), count);
  }

  private SqlSession sqlSession;
}
