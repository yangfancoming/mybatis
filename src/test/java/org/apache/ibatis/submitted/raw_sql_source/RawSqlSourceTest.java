
package org.apache.ibatis.submitted.raw_sql_source;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.session.Configuration;
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

  // 测试 静态文本sql
  @Test
  void shouldUseRawSqlSourceForAnStaticStatement() {
    test("getUser1", RawSqlSource.class);
  }

  // 测试 内联参数(${}) 动态sql
  @Test
  void shouldUseDynamicSqlSourceForAnStatementWithInlineArguments() {
    test("getUser2", DynamicSqlSource.class);
  }

  // 测试 xml标签 动态sql
  @Test
  void shouldUseDynamicSqlSourceForAnStatementWithXmlTags() {
    test("getUser3", DynamicSqlSource.class);
  }

  Configuration config = sqlSession.getConfiguration();

  private void test(String statement, Class<? extends SqlSource> sqlSource) {
    Assertions.assertEquals(sqlSource, config.getMappedStatement(statement).getSqlSource().getClass());
      String sql = config.getMappedStatement(statement).getSqlSource().getBoundSql('?').getSql();
      Assertions.assertEquals("select * from users where id = ?", sql);
      User user = sqlSession.selectOne(statement, 1);
      Assertions.assertEquals("User1", user.getName());
  }

}
