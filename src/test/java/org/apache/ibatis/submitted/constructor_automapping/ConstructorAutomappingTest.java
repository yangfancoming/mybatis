
package org.apache.ibatis.submitted.constructor_automapping;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstructorAutomappingTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources .getResourceAsReader("org/apache/ibatis/submitted/constructor_automapping/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/constructor_automapping/CreateDB.sql");
  }

  @Test
  void shouldHandleColumnPrefixCorrectly() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);

      List<Article> articles = mapper.nestedConstructorAutomapping();
      assertEquals(2, articles.size());

      Article article1 = articles.get(0);
      assertEquals("Article1", article1.getTitle());

      Author author1 = article1.getAuthor();
      assertEquals(Integer.valueOf(100), author1.getId());
      assertEquals("Author1", author1.getName());
    }
  }

}
