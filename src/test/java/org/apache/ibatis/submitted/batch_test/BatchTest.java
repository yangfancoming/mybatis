
package org.apache.ibatis.submitted.batch_test;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BatchTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/batch_test/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/batch_test/CreateDB.sql");
  }

  @Test
  void shouldGetAUserNoException() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false)) {
      try {
        Mapper mapper = sqlSession.getMapper(Mapper.class);
        User user = mapper.getUser(1);
        user.setId(2);
        user.setName("User2");
        mapper.insertUser(user);
        Assertions.assertEquals("Dept1", mapper.getUser(2).getDept().getName());
      } finally {
        sqlSession.commit();
      }
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

}
