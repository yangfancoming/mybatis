
package org.apache.ibatis.submitted.lazyload_proxyfactory_comparison;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class AbstractLazyTest {

  private SqlSession sqlSession;
  private Mapper mapper;

  protected abstract String getConfiguration();

  @BeforeEach
  void before() throws Exception {

   SqlSessionFactory sqlSessionFactory;
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/lazyload_proxyfactory_comparison/mybatis-config-" + getConfiguration() + ".xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/lazyload_proxyfactory_comparison/CreateDB.sql");
    sqlSession = sqlSessionFactory.openSession();
    mapper = sqlSession.getMapper(Mapper.class);
  }

  @AfterEach
  void after() {
    if (sqlSession != null) {
      sqlSession.close();
    }
  }

  @Test
  void lazyLoadUserWithGetObjectWithInterface() {
    Assertions.assertNotNull(mapper.getUserWithGetObjectWithInterface(1).getOwner());
  }

  @Test
  void lazyLoadUserWithGetObjectWithoutInterface() {
    Assertions.assertNotNull(mapper.getUserWithGetObjectWithoutInterface(1).getOwner());
  }

  @Test
  void lazyLoadUserWithGetXxxWithInterface() {
    Assertions.assertNotNull(mapper.getUserWithGetXxxWithInterface(1).getOwner());
  }

  @Test
  void lazyLoadUserWithGetXxxWithoutInterface() {
    Assertions.assertNotNull(mapper.getUserWithGetXxxWithoutInterface(1).getOwner());
  }

  @Test
  void lazyLoadUserWithNothingWithInterface() {
    Assertions.assertNotNull(mapper.getUserWithNothingWithInterface(1).getOwner());
  }

  @Test
  void lazyLoadUserWithNothingWithoutInterface() {
    Assertions.assertNotNull(mapper.getUserWithNothingWithoutInterface(1).getOwner());
  }
}
