
package org.apache.ibatis.submitted.multidb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MultiDbTest {

  protected static SqlSessionFactory sqlSessionFactory;
  protected static SqlSessionFactory sqlSessionFactory2;

  @BeforeAll
  public static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multidb/MultiDbConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/multidb/CreateDB.sql");
  }

  @Test
  public void shouldExecuteHsqlQuery() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      String answer = mapper.select1(1);
      assertEquals("hsql", answer);
    }
  }

  @Test
  public void shouldExecuteCommonQuery() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      String answer = mapper.select2(1);
      assertEquals("common", answer);
    }
  }

  @Test
  public void shouldExecuteHsqlQueryWithDynamicIf() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      String answer = mapper.select3(1);
      assertEquals("hsql", answer);
    }
  }

  @Test
  public void shouldExecuteHsqlQueryWithInclude() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      String answer = mapper.select4(1);
      assertEquals("hsql", answer);
    }
  }

  @Test
  public void shouldInsertInCommonWithSelectKey() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      mapper.insert(new User(2, "test"));
      String answer = mapper.select2(1);
      assertEquals("common", answer);
    }
  }

  @Test
  public void shouldInsertInCommonWithSelectKey2() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      mapper.insert2(new User(2, "test"));
      String answer = mapper.select2(1);
      assertEquals("common", answer);
    }
  }

}
