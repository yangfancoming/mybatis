
package org.apache.ibatis.submitted.blocking_cache;

import java.io.Reader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// issue #524
class BlockingCacheTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeEach
  void setUp() throws Exception {
    // create a SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/blocking_cache/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/blocking_cache/CreateDB.sql");
  }

  @Test
  void testBlockingCache() {
    ExecutorService defaultThreadPool = Executors.newFixedThreadPool(2);

    long init = System.currentTimeMillis();

    for (int i = 0; i < 2; i++) {
      defaultThreadPool.execute(this::accessDB);
    }

    defaultThreadPool.shutdown();

    while (!defaultThreadPool.isTerminated()) {
    }

    long totalTime = System.currentTimeMillis() - init;
    Assertions.assertTrue(totalTime > 1000);
  }

  private void accessDB() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper pm = sqlSession.getMapper(PersonMapper.class);
      pm.findAll();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Assertions.fail(e.getMessage());
      }
    }
  }

}
