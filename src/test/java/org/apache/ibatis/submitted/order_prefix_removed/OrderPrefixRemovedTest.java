
package org.apache.ibatis.submitted.order_prefix_removed;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrderPrefixRemovedTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void initDatabase() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/order_prefix_removed/ibatisConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/order_prefix_removed/CreateDB.sql");
  }

  @Test
  void testOrderPrefixNotRemoved() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE)) {
      PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
      Person person = personMapper.select("slow");
      assertNotNull(person);
      sqlSession.commit();
    }
  }
}
