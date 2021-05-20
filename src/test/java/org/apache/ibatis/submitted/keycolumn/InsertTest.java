
package org.apache.ibatis.submitted.keycolumn;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.testcontainers.PgContainer;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Tag("TestcontainersTests")
class InsertTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    Configuration configuration = new Configuration();
    Environment environment = new Environment("development", new JdbcTransactionFactory(), PgContainer.getUnpooledDataSource());
    configuration.setEnvironment(environment);
    configuration.addMapper(InsertMapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/keycolumn/CreateDB.sql");
  }

  @Test
  void testInsertAnnotated() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      InsertMapper mapper = sqlSession.getMapper(InsertMapper.class);
      Name name = new Name();
      name.setFirstName("Fred");
      name.setLastName("Flintstone");
      int rows = mapper.insertNameAnnotated(name);
      assertNotNull(name.getId());
      assertEquals(1, rows);
    }
  }

  @Test
  void testInsertMapped() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      InsertMapper mapper = sqlSession.getMapper(InsertMapper.class);
      Name name = new Name();
      name.setFirstName("Fred");
      name.setLastName("Flintstone");
      int rows = mapper.insertNameMapped(name);
      assertNotNull(name.getId());
      assertEquals(1, rows);
    }
  }

  @Test
  void testInsertMappedBatch() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      InsertMapper mapper = sqlSession.getMapper(InsertMapper.class);
      Name name = new Name();
      name.setFirstName("Fred");
      name.setLastName("Flintstone");
      mapper.insertNameMapped(name);
      Name name2 = new Name();
      name2.setFirstName("Wilma");
      name2.setLastName("Flintstone");
      mapper.insertNameMapped(name2);
      List<BatchResult> batchResults = sqlSession.flushStatements();
      assertNotNull(name.getId());
      assertNotNull(name2.getId());
      assertEquals(1, batchResults.size());
    }
  }
}
