
package org.apache.ibatis.submitted.multidb;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiDbTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/multidb/MultiDbConfig.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/multidb/CreateDB.sql";

  MultiDbMapper mapper;
  
  @BeforeEach
  public void beforeAll() throws Exception {
    setUpByInputStream(XMLPATH,DBSQL);
    mapper = sqlSession.getMapper(MultiDbMapper.class);
  }

  @Test
  public void shouldExecuteHsqlQuery() {
    String answer = mapper.select1(1);
    assertEquals("hsql", answer);
  }

  @Test
  public void shouldExecuteCommonQuery() {
    String answer = mapper.select2(1);
    assertEquals("common", answer);
  }

  @Test
  public void shouldExecuteHsqlQueryWithDynamicIf() {
    String answer = mapper.select3(1);
    assertEquals("hsql", answer);
  }

  @Test
  public void shouldExecuteHsqlQueryWithInclude() {
    String answer = mapper.select4(1);
    assertEquals("hsql", answer);
  }

  @Test
  public void shouldInsertInCommonWithSelectKey() {
    mapper.insert(new User(2, "test"));
    String answer = mapper.select2(1);
    assertEquals("common", answer);
  }

  @Test
  public void shouldInsertInCommonWithSelectKey2() {
    mapper.insert2(new User(2, "test"));
    String answer = mapper.select2(1);
    assertEquals("common", answer);
  }
}
