
package org.apache.ibatis.submitted.valueinmap;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ValueInMapTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/valueinmap/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/valueinmap/CreateDB.sql";

  @BeforeAll
  static void setUp() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
  }

  @Test // issue #165
  void shouldWorkWithAPropertyNamedValue() {
    Map<String, String> map = new HashMap<>();
    map.put("table", "users");
    map.put("column", "name");
    map.put("value", "User1");
    Integer count = sqlSession.selectOne("count", map);
    Assertions.assertEquals(1, count);
  }

  @Test
  void shouldWorkWithAList() {
    List<String> list = new ArrayList<>();
    list.add("users");
    Assertions.assertThrows(PersistenceException.class, () -> sqlSession.selectOne("count2",list));
  }

}
