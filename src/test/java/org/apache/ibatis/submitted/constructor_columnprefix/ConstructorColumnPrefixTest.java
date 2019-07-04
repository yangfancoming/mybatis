
package org.apache.ibatis.submitted.constructor_columnprefix;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConstructorColumnPrefixTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources
        .getResourceAsReader("org/apache/ibatis/submitted/constructor_columnprefix/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
        "org/apache/ibatis/submitted/constructor_columnprefix/CreateDB.sql");
  }

  @Test
  void shouldGetArticles() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Article> articles = mapper.getArticles();
      assertArticles(articles);
    }
  }

  @Test
  void shouldGetArticlesAnno() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Article> articles = mapper.getArticlesAnno();
      assertArticles(articles);
    }
  }

  void assertArticles(List<Article> articles) {
    assertEquals(2, articles.size());
    Article article1 = articles.get(0);
    assertEquals(Integer.valueOf(1), article1.getId().getId());
    assertEquals("Article 1", article1.getName());
    assertEquals("Mary", article1.getAuthor().getName());
    assertEquals("Bob", article1.getCoauthor().getName());
    Article article2 = articles.get(1);
    assertEquals(Integer.valueOf(2), article2.getId().getId());
    assertEquals("Article 2", article2.getName());
    assertEquals("Jane", article2.getAuthor().getName());
    assertEquals("Mary", article2.getCoauthor().getName());
  }

}
