
package org.apache.ibatis.submitted.raw_sql_source;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RawSqlSourceTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/raw_sql_source/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/raw_sql_source/CreateDB.sql";

  @BeforeAll
  static void setUp() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
  }

  @Test
  void shouldUseRawSqlSourceForAnStaticStatement() {
    test("getUser1", RawSqlSource.class);
  }

  @Test
  void shouldUseDynamicSqlSourceForAnStatementWithInlineArguments() {
    test("getUser2", DynamicSqlSource.class);
  }

  @Test
  void shouldUseDynamicSqlSourceForAnStatementWithXmlTags() {
    test("getUser3", DynamicSqlSource.class);
  }

  // doit 了解  框架中的各种组件的灵活运用。。。。。
  private void test(String statement, Class<? extends SqlSource> sqlSource) {
      Assertions.assertEquals(sqlSource, sqlSession.getConfiguration().getMappedStatement(statement).getSqlSource().getClass());
      String sql = sqlSession.getConfiguration().getMappedStatement(statement).getSqlSource().getBoundSql('?').getSql();
      Assertions.assertEquals("select * from users where id = ?", sql);
      User user = sqlSession.selectOne(statement, 1);
      Assertions.assertEquals("User1", user.getName());
  }

}
