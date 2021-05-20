
package org.apache.ibatis.submitted.cursor_simple;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.testcontainers.PgContainer;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("TestcontainersTests")
class PostgresCursorTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    Configuration configuration = new Configuration();
    Environment environment = new Environment("development", new JdbcTransactionFactory(),
        PgContainer.getUnpooledDataSource());
    configuration.setEnvironment(environment);
    configuration.addMapper(Mapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
      "org/apache/ibatis/submitted/cursor_simple/CreateDB.sql");
  }

  @Test
  void shouldBeAbleToReuseStatement() throws IOException {
    // #1351
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE)) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      {
        Cursor<User> usersCursor = mapper.getAllUsers();
        Iterator<User> iterator = usersCursor.iterator();
        User user = iterator.next();
        assertEquals("User1", user.getName());
        usersCursor.close();
      }
      {
        Cursor<User> usersCursor = mapper.getAllUsers();
        Iterator<User> iterator = usersCursor.iterator();
        User user = iterator.next();
        assertEquals("User1", user.getName());
        usersCursor.close();
      }
    }
  }
}
