
package org.apache.ibatis.submitted.deferload_common_property;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CommonPropertyDeferLoadTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSessionFactory lazyLoadSqlSessionFactory;

  @BeforeAll
  static void initDatabase() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/deferload_common_property/ibatisConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/deferload_common_property/lazyLoadIbatisConfig.xml")) {
      lazyLoadSqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/deferload_common_property/CreateDB.sql");
  }

  @Test
  void testDeferLoadAfterResultHandler() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      class MyResultHandler implements ResultHandler {
        private List<Child> children = new ArrayList<>();
        @Override
        public void handleResult(ResultContext context) {
          Child child = (Child)context.getResultObject();
          children.add(child);
        }
      }
      MyResultHandler myResultHandler = new MyResultHandler();
      sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", myResultHandler);
      for (Child child: myResultHandler.children) {
        assertNotNull(child.getFather());
      }
    }
  }

  @Test
  void testDeferLoadDuringResultHandler() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      class MyResultHandler implements ResultHandler {
        @Override
        public void handleResult(ResultContext context) {
          Child child = (Child)context.getResultObject();
          assertNotNull(child.getFather());
        }
      }
      sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", new MyResultHandler());
    }
  }

  @Test
  void testDeferLoadAfterResultHandlerWithLazyLoad() {
    try (SqlSession sqlSession = lazyLoadSqlSessionFactory.openSession()) {
      class MyResultHandler implements ResultHandler {
        private List<Child> children = new ArrayList<>();
        @Override
        public void handleResult(ResultContext context) {
          Child child = (Child)context.getResultObject();
          children.add(child);
        }
      }
      MyResultHandler myResultHandler = new MyResultHandler();
      sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", myResultHandler);
      for (Child child: myResultHandler.children) {
        assertNotNull(child.getFather());
      }
    }
  }

  @Test
  void testDeferLoadDuringResultHandlerWithLazyLoad() {
    try (SqlSession sqlSession = lazyLoadSqlSessionFactory.openSession()) {
      class MyResultHandler implements ResultHandler {
        @Override
        public void handleResult(ResultContext context) {
          Child child = (Child)context.getResultObject();
          assertNotNull(child.getFather());
        }
      }
      sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", new MyResultHandler());
    }
  }
}
