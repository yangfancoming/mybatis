
package org.apache.ibatis.submitted.raw_sql_source;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RawSqlSourceTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/raw_sql_source/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/raw_sql_source/CreateDB.sql");
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

  private void test(String statement, Class<? extends SqlSource> sqlSource) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Assertions.assertEquals(sqlSource, sqlSession.getConfiguration().getMappedStatement(statement).getSqlSource().getClass());
      String sql = sqlSession.getConfiguration().getMappedStatement(statement).getSqlSource().getBoundSql('?').getSql();
      Assertions.assertEquals("select * from users where id = ?", sql);
      User user = sqlSession.selectOne(statement, 1);
      Assertions.assertEquals("User1", user.getName());
    }
  }

}
