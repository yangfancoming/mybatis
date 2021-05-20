
package org.apache.ibatis.submitted.bringrags;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;

class SimpleObjectTest {
  private SimpleChildObjectMapper simpleChildObjectMapper;
  private SqlSession sqlSession;
  private Connection conn;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/bringrags/mybatis-config.xml")) {
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      conn = sqlSession.getConnection();
      ScriptRunner runner = new ScriptRunner(conn);
      runner.setLogWriter(null);
      runner.runScript(new StringReader("DROP TABLE IF EXISTS SimpleObject;"));
      runner.runScript(new StringReader("DROP TABLE IF EXISTS SimpleChildObject;"));
      runner.runScript(new StringReader("CREATE TABLE SimpleObject (id VARCHAR(5) NOT NULL);"));
      runner.runScript(new StringReader("CREATE TABLE SimpleChildObject (id VARCHAR(5) NOT NULL, simple_object_id VARCHAR(5) NOT NULL);"));
      runner.runScript(new StringReader("INSERT INTO SimpleObject (id) values ('10000');"));
      runner.runScript(new StringReader("INSERT INTO SimpleChildObject (id, simple_object_id) values ('20000', '10000');"));
      simpleChildObjectMapper = sqlSession.getMapper(SimpleChildObjectMapper.class);
    }
  }

  @AfterEach
  void tearDown() throws Exception {
    conn.close();
    sqlSession.close();
  }

  @Test
  void testGetById() {
    SimpleChildObject sc = simpleChildObjectMapper.getSimpleChildObjectById("20000");
    Assertions.assertNotNull(sc);
    Assertions.assertNotNull(sc.getSimpleObject());
  }

}
