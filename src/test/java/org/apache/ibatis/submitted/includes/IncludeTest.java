
package org.apache.ibatis.submitted.includes;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class IncludeTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/includes/MapperConfig.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/includes/CreateDB.sql";

  @BeforeEach
  public void beforeAll() throws Exception {
    setUpByInputStream(XMLPATH,DBSQL);
  }

  @Test
  void testIncludes()  {
    final Integer result = sqlSession.selectOne("org.apache.ibatis.submitted.includes.mapper.selectWithProperty");
    Assertions.assertEquals(Integer.valueOf(1), result);
  }

  @Test
  void testParametrizedIncludes() {
    final Map<String, Object> result = sqlSession.selectOne("org.apache.ibatis.submitted.includes.mapper.select");
    Assertions.assertEquals(Integer.valueOf(3), result.size());
  }
}
