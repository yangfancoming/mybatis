
package org.apache.ibatis.submitted.heavy_initial_load;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class HeavyInitialLoadTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void initSqlSessionFactory() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/heavy_initial_load/ibatisConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    sqlSessionFactory.getConfiguration().getEnvironment().getDataSource().getConnection().close();
  }

  private static final int THREAD_COUNT = 5;

  /**
   * Test to demonstrate the effect of the
   * https://issues.apache.org/jira/browse/OGNL-121 issue in ognl on mybatis.
   * Use the thing mapper for the first time in multiple threads. The mapper contains
   * a lot of ognl references to static final class members like:
   * <code>@org.apache.ibatis.submitted.heavy_initial_load.Code@_1.equals(code)</code>
   * Handling of these references is optimized in ognl (because they never change), but
   * version 2.6.9 has a bug in caching the result . As a result the reference is
   * translated to a 'null' value, which is used to invoke the 'equals' method on
   * (hence the 'target is null for method equals' exception).
   */
  @Test
  void selectThingsConcurrently_mybatis_issue_224() throws Exception {
    final List<Throwable> throwables = Collections.synchronizedList(new ArrayList<>());

    Thread[] threads = new Thread[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads[i] = new Thread(() -> {
        try {
          selectThing();
        } catch(Exception exception) {
          throwables.add(exception);
        }
      });
      threads[i].start();
    }
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads[i].join();
    }
    Assertions.assertTrue(throwables.isEmpty(), "There were exceptions: " + throwables);
  }

  void selectThing() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      ThingMapper mapper = sqlSession.getMapper(ThingMapper.class);
      Thing selected = mapper.selectByCode(Code._1);
      Assertions.assertEquals(1, selected.getId().longValue());
    }
  }
}
