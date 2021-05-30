
package org.apache.ibatis.submitted.nested_query_cache;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NestedQueryCacheTest extends BaseDataTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/nested_query_cache/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    createBlogDataSource();
  }

  @Test
  void testThatNestedQueryItemsAreRetrievedFromCache() {
    final Author author;
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final AuthorMapper authorMapper = sqlSession.getMapper(AuthorMapper.class);
      author = authorMapper.selectAuthor(101);
      // ensure that author is cached
      final Author cachedAuthor = authorMapper.selectAuthor(101);
      assertThat(author).isSameAs(cachedAuthor);
    }

    // open a new session
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
      // ensure that nested author within blog is cached
      assertThat(blogMapper.selectBlog(1).getAuthor()).isSameAs(author);
      assertThat(blogMapper.selectBlogUsingConstructor(1).getAuthor()).isSameAs(author);
    }
  }

  @Test
  void testThatNestedQueryItemsAreRetrievedIfNotInCache() {
    Author author;
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
      author = blogMapper.selectBlog(1).getAuthor();
      // ensure that nested author within blog is cached
      assertNotNull(blogMapper.selectBlog(1).getAuthor(), "blog author");
      assertNotNull(blogMapper.selectBlogUsingConstructor(1).getAuthor(), "blog author");
    }

    // open a new session
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final AuthorMapper authorMapper = sqlSession.getMapper(AuthorMapper.class);
      Author cachedAuthor = authorMapper.selectAuthor(101);
      // ensure that nested author within blog is cached
      assertThat(cachedAuthor).isSameAs(author);
    }

  }
}
