
package org.apache.ibatis.submitted.result_set_type;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;
import java.util.function.Function;

class ResultSetTypeTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/result_set_type/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    ScriptRunner runner = new ScriptRunner(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource().getConnection());
    runner.setDelimiter("go");
    runner.setLogWriter(null);
    runner.setErrorLogWriter(null);
    BaseDataTest.runScript(runner, "org/apache/ibatis/submitted/result_set_type/CreateDB.sql");
  }

  @Test
  void testWithStatement() {
    test(mapper -> mapper.getUserWithStatementAndUnset(new RowBounds(5, 3)), 0);
    test(mapper -> mapper.getUserWithStatementAndDefault(new RowBounds(4, 3)), 1);
    test(mapper -> mapper.getUserWithStatementAndForwardOnly(new RowBounds(3, 3)), 2);
    test(mapper -> mapper.getUserWithStatementAndScrollInsensitive(new RowBounds(2, 2)), 2);
    test(mapper -> mapper.getUserWithStatementAndScrollSensitive(new RowBounds(1, 1)), 1);
  }

  @Test
  void testWithPrepared() {
    test(mapper -> mapper.getUserWithPreparedAndUnset(new RowBounds(5, 3)), 0);
    test(mapper -> mapper.getUserWithPreparedAndDefault(new RowBounds(4, 3)), 1);
    test(mapper -> mapper.getUserWithPreparedAndForwardOnly(new RowBounds(3, 3)), 2);
    test(mapper -> mapper.getUserWithPreparedAndScrollInsensitive(new RowBounds(2, 2)), 2);
    test(mapper -> mapper.getUserWithPreparedAndScrollSensitive(new RowBounds(1, 1)), 1);
  }

  @Test
  void testWithCallable() {
    test(mapper -> mapper.getUserWithCallableAndUnset(new RowBounds(5, 3)), 0);
    test(mapper -> mapper.getUserWithCallableAndDefault(new RowBounds(4, 3)), 1);
    test(mapper -> mapper.getUserWithCallableAndForwardOnly(new RowBounds(3, 3)), 2);
    test(mapper -> mapper.getUserWithCallableAndScrollInsensitive(new RowBounds(2, 2)), 2);
    test(mapper -> mapper.getUserWithCallableAndScrollSensitive(new RowBounds(1, 1)), 1);
  }

  private void test(Function<Mapper, List<User>> usersSupplier, int expectedSize) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = usersSupplier.apply(mapper);
      Assertions.assertEquals(expectedSize, users.size());
    }
  }

}
