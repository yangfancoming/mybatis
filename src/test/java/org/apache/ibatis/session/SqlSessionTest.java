
package org.apache.ibatis.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.util.*;

import javassist.util.proxy.Proxy;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Blog;
import org.apache.ibatis.domain.blog.Comment;
import org.apache.ibatis.domain.blog.DraftPost;
import org.apache.ibatis.domain.blog.ImmutableAuthor;
import org.apache.ibatis.domain.blog.Post;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.domain.blog.Tag;
import org.apache.ibatis.domain.blog.mappers.AuthorMapper;
import org.apache.ibatis.domain.blog.mappers.AuthorMapperWithMultipleHandlers;
import org.apache.ibatis.domain.blog.mappers.AuthorMapperWithRowBounds;
import org.apache.ibatis.domain.blog.mappers.BlogMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SqlSessionTest extends BaseDataTest {
  private static SqlSessionFactory sqlMapper;
  private static SqlSession session;

  @BeforeAll
  static void setup() throws Exception {
    createBlogDataSource();
    final String resource = "org/apache/ibatis/builder/MapperConfig.xml";
    final Reader reader = Resources.getResourceAsReader(resource);
    sqlMapper = new SqlSessionFactoryBuilder().build(reader);
    session = sqlMapper.openSession();
  }

  @Test
  void shouldResolveBothSimpleNameAndFullyQualifiedName() {
    Configuration c = new Configuration();
    final String fullName = "com.mycache.MyCache";
    final String shortName = "MyCache";
    final PerpetualCache cache = new PerpetualCache(fullName);
    c.addCache(cache);
    assertEquals(cache, c.getCache(fullName));
    assertEquals(cache, c.getCache(shortName));
  }

  @Test
  void shouldFailOverToMostApplicableSimpleName() {
    Configuration c = new Configuration();
    final String fullName = "com.mycache.MyCache";
    final String invalidName = "unknown.namespace.MyCache";
    final PerpetualCache cache = new PerpetualCache(fullName);
    c.addCache(cache);
    assertEquals(cache, c.getCache(fullName));
    Assertions.assertThrows(IllegalArgumentException.class, () -> c.getCache(invalidName));
  }

  @Test
  void shouldSucceedWhenFullyQualifiedButFailDueToAmbiguity() {
    Configuration c = new Configuration();

    final String name1 = "com.mycache.MyCache";
    final PerpetualCache cache1 = new PerpetualCache(name1);
    c.addCache(cache1);

    final String name2 = "com.other.MyCache";
    final PerpetualCache cache2 = new PerpetualCache(name2);
    c.addCache(cache2);

    final String shortName = "MyCache";

    assertEquals(cache1, c.getCache(name1));
    assertEquals(cache2, c.getCache(name2));

    try {
      c.getCache(shortName);
      fail("Exception expected.");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("ambiguous"));
    }

  }

  @Test
  void shouldFailToAddDueToNameConflict() {
    Configuration c = new Configuration();
    final String fullName = "com.mycache.MyCache";
    final PerpetualCache cache = new PerpetualCache(fullName);
    try {
      c.addCache(cache);
      c.addCache(cache);
      fail("Exception expected.");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("already contains value"));
    }
  }

  @Test
  void shouldOpenAndClose() {
    SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.SERIALIZABLE);
    session.close();
  }

  @Test
  void shouldCommitAnUnUsedSqlSession() {
    try (SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
      session.commit(true);
    }
  }

  @Test
  void shouldRollbackAnUnUsedSqlSession() {
    try (SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
      session.rollback(true);
    }
  }

  @Test
  void shouldSelectAllAuthors() {
    try (SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
      List<Author> authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAllAuthors");
      assertEquals(2, authors.size());
    }
  }

  @Test
  void shouldFailWithTooManyResultsException() {
    try (SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
      Assertions.assertThrows(TooManyResultsException.class, () -> {
        session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAllAuthors");
      });
    }
  }

  @Test
  void shouldSelectAllAuthorsAsMap() {
    try (SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.SERIALIZABLE)) {
      final Map<Integer,Author> authors = session.selectMap("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAllAuthors", "id");
      assertEquals(2, authors.size());
      for(Map.Entry<Integer,Author> authorEntry : authors.entrySet()) {
        assertEquals(authorEntry.getKey(), (Integer) authorEntry.getValue().getId());
      }
    }
  }

  @Test
  void shouldSelectCountOfPosts() {
      Integer count = session.selectOne("org.apache.ibatis.domain.blog.mappers.BlogMapper.selectCountOfPosts");
      assertEquals(5, count.intValue());
  }

  @Test
  void shouldEnsureThatBothEarlyAndLateResolutionOfNesteDiscriminatorsResolesToUseNestedResultSetHandler() {
    try (SqlSession session = sqlMapper.openSession()) {
      Configuration configuration = sqlMapper.getConfiguration();
      assertTrue(configuration.getResultMap("org.apache.ibatis.domain.blog.mappers.BlogMapper.earlyNestedDiscriminatorPost").hasNestedResultMaps());
      assertTrue(configuration.getResultMap("org.apache.ibatis.domain.blog.mappers.BlogMapper.lateNestedDiscriminatorPost").hasNestedResultMaps());
    }
  }

  @Test
  void shouldSelectOneAuthor() {
      Author author = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", new Author(101));
      assertEquals(101, author.getId());
      assertEquals(Section.NEWS, author.getFavouriteSection());
  }

  @Test
  void shouldSelectOneAuthorAsList() {
      List<Author> authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", new Author(101));
      assertEquals(101, authors.get(0).getId());
      assertEquals(Section.NEWS, authors.get(0).getFavouriteSection());
  }

  @Test
  void shouldSelectOneImmutableAuthor() {
      ImmutableAuthor author = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectImmutableAuthor", new Author(101));
      assertEquals(101, author.getId());
      assertEquals(Section.NEWS, author.getFavouriteSection());
  }

  @Test
  void shouldSelectOneAuthorWithInlineParams() {
      Author author = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthorWithInlineParams", new Author(101));
      assertEquals(101, author.getId());
  }

  @Test
  void shouldInsertAuthor() {
      Author expected = new Author(500, "cbegin", "******", "cbegin@somewhere.com", "Something...", null);
      int updates = session.insert("org.apache.ibatis.domain.blog.mappers.AuthorMapper.insertAuthor", expected);
      assertEquals(1, updates);
      Author actual = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", new Author(500));
      assertNotNull(actual);
      assertEquals(expected.getId(), actual.getId());
      assertEquals(expected.getUsername(), actual.getUsername());
      assertEquals(expected.getPassword(), actual.getPassword());
      assertEquals(expected.getEmail(), actual.getEmail());
      assertEquals(expected.getBio(), actual.getBio());
  }

  @Test
  void shouldUpdateAuthorImplicitRollback() {
    try (SqlSession session = sqlMapper.openSession()) {
      Author original = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      original.setEmail("new@email.com");
      int updates = session.update("org.apache.ibatis.domain.blog.mappers.AuthorMapper.updateAuthor", original);
      assertEquals(1, updates);
      Author updated = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      assertEquals(original.getEmail(), updated.getEmail());
    }
    try (SqlSession session = sqlMapper.openSession()) {
      Author updated = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      assertEquals("jim@ibatis.apache.org", updated.getEmail());
    }
  }

  @Test
  void shouldUpdateAuthorCommit() {
    Author original;
    try (SqlSession session = sqlMapper.openSession()) {
      original = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      original.setEmail("new@email.com");
      int updates = session.update("org.apache.ibatis.domain.blog.mappers.AuthorMapper.updateAuthor", original);
      assertEquals(1, updates);
      Author updated = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      assertEquals(original.getEmail(), updated.getEmail());
      session.commit();
    }
    try (SqlSession session = sqlMapper.openSession()) {
      Author updated = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      assertEquals(original.getEmail(), updated.getEmail());
    }
  }

  @Test
  void shouldUpdateAuthorIfNecessary() {
    Author original;
    try (SqlSession session = sqlMapper.openSession()) {
      original = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      original.setEmail("new@email.com");
      original.setBio(null);
      int updates = session.update("org.apache.ibatis.domain.blog.mappers.AuthorMapper.updateAuthorIfNecessary", original);
      assertEquals(1, updates);
      Author updated = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      assertEquals(original.getEmail(), updated.getEmail());
      session.commit();
    }
    try (SqlSession session = sqlMapper.openSession()) {
      Author updated = session.selectOne("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", 101);
      assertEquals(original.getEmail(), updated.getEmail());
    }
  }

  @Test
  void shouldDeleteAuthor() {
      final int id = 102;

      List<Author> authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", id);
      assertEquals(1, authors.size());

      int updates = session.delete("org.apache.ibatis.domain.blog.mappers.AuthorMapper.deleteAuthor", id);
      assertEquals(1, updates);

      authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", id);
      assertEquals(0, authors.size());

      session.rollback();
      authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor", id);
      assertEquals(1, authors.size());
  }

  @Test
  void shouldSelectBlogWithPostsAndAuthorUsingSubSelects() {
      Blog blog = session.selectOne("org.apache.ibatis.domain.blog.mappers.BlogMapper.selectBlogWithPostsUsingSubSelect", 1);
      assertEquals("Jim Business", blog.getTitle());
      assertEquals(2, blog.getPosts().size());
      assertEquals("Corn nuts", blog.getPosts().get(0).getSubject());
      assertEquals(101, blog.getAuthor().getId());
      assertEquals("jim", blog.getAuthor().getUsername());
  }

  @Test
  void shouldSelectBlogWithPostsAndAuthorUsingSubSelectsLazily() {
      Blog blog = session.selectOne("org.apache.ibatis.domain.blog.mappers.BlogMapper.selectBlogWithPostsUsingSubSelectLazily", 1);
      Assertions.assertTrue(blog instanceof Proxy);
      assertEquals("Jim Business", blog.getTitle());
      assertEquals(2, blog.getPosts().size());
      assertEquals("Corn nuts", blog.getPosts().get(0).getSubject());
      assertEquals(101, blog.getAuthor().getId());
      assertEquals("jim", blog.getAuthor().getUsername());
  }

  @Test
  void shouldSelectBlogWithPostsAndAuthorUsingJoin() {
      Blog blog = session.selectOne("org.apache.ibatis.domain.blog.mappers.BlogMapper.selectBlogJoinedWithPostsAndAuthor", 1);
      assertEquals("Jim Business", blog.getTitle());

      final Author author = blog.getAuthor();
      assertEquals(101, author.getId());
      assertEquals("jim", author.getUsername());

      final List<Post> posts = blog.getPosts();
      assertEquals(2, posts.size());

      final Post post = blog.getPosts().get(0);
      assertEquals(1, post.getId());
      assertEquals("Corn nuts", post.getSubject());

      final List<Comment> comments = post.getComments();
      assertEquals(2, comments.size());

      final List<Tag> tags = post.getTags();
      assertEquals(3, tags.size());

      final Comment comment = comments.get(0);
      assertEquals(1, comment.getId());

      assertEquals(DraftPost.class, blog.getPosts().get(0).getClass());
      assertEquals(Post.class, blog.getPosts().get(1).getClass());
  }

  @Test
  void shouldSelectNestedBlogWithPostsAndAuthorUsingJoin() {
      Blog blog = session.selectOne("org.apache.ibatis.domain.blog.mappers.NestedBlogMapper.selectBlogJoinedWithPostsAndAuthor", 1);
      assertEquals("Jim Business", blog.getTitle());

      final Author author = blog.getAuthor();
      assertEquals(101, author.getId());
      assertEquals("jim", author.getUsername());

      final List<Post> posts = blog.getPosts();
      assertEquals(2, posts.size());

      final Post post = blog.getPosts().get(0);
      assertEquals(1, post.getId());
      assertEquals("Corn nuts", post.getSubject());

      final List<Comment> comments = post.getComments();
      assertEquals(2, comments.size());

      final List<Tag> tags = post.getTags();
      assertEquals(3, tags.size());

      final Comment comment = comments.get(0);
      assertEquals(1, comment.getId());

      assertEquals(DraftPost.class, blog.getPosts().get(0).getClass());
      assertEquals(Post.class, blog.getPosts().get(1).getClass());
  }

  @Test
  void shouldThrowExceptionIfMappedStatementDoesNotExist() {
    try (SqlSession session = sqlMapper.openSession()) {
      session.selectList("ThisStatementDoesNotExist");
      fail("Expected exception to be thrown due to statement that does not exist.");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("does not contain value for ThisStatementDoesNotExist"));
    }
  }

  @Test
  void shouldThrowExceptionIfTryingToAddStatementWithSameNameInXml() {
    Configuration config = sqlMapper.getConfiguration();
    try {
      MappedStatement ms = new MappedStatement.Builder(config,"org.apache.ibatis.domain.blog.mappers.BlogMapper.selectBlogWithPostsUsingSubSelect",
        Mockito.mock(SqlSource.class), SqlCommandType.SELECT).resource("org/mybatis/TestMapper.xml").build();
      config.addMappedStatement(ms);
      fail("Expected exception to be thrown due to statement that already exists.");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("already contains value for org.apache.ibatis.domain.blog.mappers.BlogMapper.selectBlogWithPostsUsingSubSelect. please check org/apache/ibatis/builder/BlogMapper.xml and org/mybatis/TestMapper.xml"));
    }
  }

  @Test
  void shouldThrowExceptionIfTryingToAddStatementWithSameNameInAnnotation() {
    Configuration config = sqlMapper.getConfiguration();
    try {
      MappedStatement ms = new MappedStatement.Builder(config,"org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor2",
        Mockito.mock(SqlSource.class), SqlCommandType.SELECT).resource("org/mybatis/TestMapper.xml").build();
      config.addMappedStatement(ms);
      fail("Expected exception to be thrown due to statement that already exists.");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("already contains value for org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAuthor2. please check org/apache/ibatis/domain/blog/mappers/AuthorMapper.java (best guess) and org/mybatis/TestMapper.xml"));
    }
  }

  @Test
  void shouldCacheAllAuthors() {
    int first;
    try (SqlSession session = sqlMapper.openSession()) {
      List<Author> authors = session.selectList("org.apache.ibatis.builder.CachedAuthorMapper.selectAllAuthors");
      first = System.identityHashCode(authors);
      session.commit(); // commit should not be required for read/only activity.
    }
    int second;
    try (SqlSession session = sqlMapper.openSession()) {
      List<Author> authors = session.selectList("org.apache.ibatis.builder.CachedAuthorMapper.selectAllAuthors");
      second = System.identityHashCode(authors);
    }
    assertEquals(first, second);
  }

  @Test
  void shouldNotCacheAllAuthors() {
    int first;
    try (SqlSession session = sqlMapper.openSession()) {
      List<Author> authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAllAuthors");
      first = System.identityHashCode(authors);
    }
    int second;
    try (SqlSession session = sqlMapper.openSession()) {
      List<Author> authors = session.selectList("org.apache.ibatis.domain.blog.mappers.AuthorMapper.selectAllAuthors");
      second = System.identityHashCode(authors);
    }
    assertTrue(first != second);
  }
  AuthorMapper mapper = session.getMapper(AuthorMapper.class);

  @Test
  void shouldSelectAuthorsUsingMapperClass() {
      List<Author> authors = mapper.selectAllAuthors();
      assertEquals(2, authors.size());
  }

  @Test
  void shouldExecuteSelectOneAuthorUsingMapperClass() {
      Author author = mapper.selectAuthor(101);
      assertEquals(101, author.getId());
  }


  @Test
  void shouldExecuteSelectOneAuthorUsingMapperClassThatReturnsALinedHashMap() {
      LinkedHashMap<String, Object> author = mapper.selectAuthorLinkedHashMap(101);
      assertEquals(101, author.get("ID"));
  }

  @Test
  void shouldExecuteSelectAllAuthorsUsingMapperClassThatReturnsSet() {
    Collection<Author> authors = mapper.selectAllAuthorsSet();
    assertEquals(2, authors.size());
  }

  @Test
  void shouldExecuteSelectAllAuthorsUsingMapperClassThatReturnsVector() {
    Collection<Author> authors = mapper.selectAllAuthorsVector();
    assertEquals(2, authors.size());
  }

  @Test
  void shouldExecuteSelectAllAuthorsUsingMapperClassThatReturnsLinkedList() {
    Collection<Author> authors = mapper.selectAllAuthorsLinkedList();
    assertEquals(2, authors.size());
  }

  @Test
  void shouldExecuteSelectAllAuthorsUsingMapperClassThatReturnsAnArray() {
    Author[] authors = mapper.selectAllAuthorsArray();
    assertEquals(2, authors.length);
  }

  @Test
  void shouldExecuteSelectOneAuthorUsingMapperClassWithResultHandler() {
    DefaultResultHandler handler = new DefaultResultHandler();
    mapper.selectAuthor(101, handler);
    Author author = (Author) handler.getResultList().get(0);
    assertEquals(101, author.getId());
  }

  @Test
  void shouldFailExecutingAnAnnotatedMapperClassWithResultHandler() {
    DefaultResultHandler handler = new DefaultResultHandler();
    Assertions.assertThrows(BindingException.class, () -> {
      mapper.selectAuthor2(101, handler);
    });
  }

  @Test
  void shouldSelectAuthorsUsingMapperClassWithResultHandler() {
    DefaultResultHandler handler = new DefaultResultHandler();
    mapper.selectAllAuthors(handler);
    assertEquals(2, handler.getResultList().size());
  }

  @Test
  void shouldFailSelectOneAuthorUsingMapperClassWithTwoResultHandlers() {
    Configuration configuration = new Configuration(sqlMapper.getConfiguration().getEnvironment());
    configuration.addMapper(AuthorMapperWithMultipleHandlers.class);
    SqlSessionFactory sqlMapperWithMultipleHandlers = new DefaultSqlSessionFactory(configuration);
    try (SqlSession sqlSession = sqlMapperWithMultipleHandlers.openSession()) {
      DefaultResultHandler handler1 = new DefaultResultHandler();
      DefaultResultHandler handler2 = new DefaultResultHandler();
      AuthorMapperWithMultipleHandlers mapper = sqlSession.getMapper(AuthorMapperWithMultipleHandlers.class);
      Assertions.assertThrows(BindingException.class, () -> mapper.selectAuthor(101, handler1, handler2));
    }
  }

  @Test
  void shouldFailSelectOneAuthorUsingMapperClassWithTwoRowBounds() {
    Configuration configuration = new Configuration(sqlMapper.getConfiguration().getEnvironment());
    configuration.addMapper(AuthorMapperWithRowBounds.class);
    SqlSessionFactory sqlMapperWithMultipleHandlers = new DefaultSqlSessionFactory(configuration);
    try (SqlSession sqlSession = sqlMapperWithMultipleHandlers.openSession()) {
      RowBounds bounds1 = new RowBounds(0, 1);
      RowBounds bounds2 = new RowBounds(0, 1);
      AuthorMapperWithRowBounds mapper = sqlSession.getMapper(AuthorMapperWithRowBounds.class);
      Assertions.assertThrows(BindingException.class, () -> mapper.selectAuthor(101, bounds1, bounds2));
    }
  }

  @Test
  void shouldInsertAuthorUsingMapperClass() {
    Author expected = new Author(500, "cbegin", "******", "cbegin@somewhere.com", "Something...", null);
    mapper.insertAuthor(expected);
    Author actual = mapper.selectAuthor(500);
    assertNotNull(actual);
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getUsername(), actual.getUsername());
    assertEquals(expected.getPassword(), actual.getPassword());
    assertEquals(expected.getEmail(), actual.getEmail());
    assertEquals(expected.getBio(), actual.getBio());
  }

  @Test
  void shouldDeleteAuthorUsingMapperClass() {
    int count = mapper.deleteAuthor(101);
    assertEquals(1, count);
    assertNull(mapper.selectAuthor(101));
  }

  @Test
  void shouldUpdateAuthorUsingMapperClass() {
    Author expected = mapper.selectAuthor(101);
    expected.setUsername("NewUsername");
    int count = mapper.updateAuthor(expected);
    assertEquals(1, count);
    Author actual = mapper.selectAuthor(101);
    assertEquals(expected.getUsername(), actual.getUsername());
  }

  @Test
  void shouldSelectAllPostsUsingMapperClass() {
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    List<Map> posts = mapper.selectAllPosts();
    assertEquals(5, posts.size());
  }

  @Test
  void shouldLimitResultsUsingMapperClass() {
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    List<Map> posts = mapper.selectAllPosts(new RowBounds(0, 2), null);
    assertEquals(2, posts.size());
    assertEquals(1, posts.get(0).get("ID"));
    assertEquals(2, posts.get(1).get("ID"));
  }

  private static class TestResultHandler implements ResultHandler {
    int count = 0;
    @Override
    public void handleResult(ResultContext context) {
      count++;
    }
  }

  @Test
  void shouldHandleZeroParameters() {
    final TestResultHandler resultHandler = new TestResultHandler();
    session.select("org.apache.ibatis.domain.blog.mappers.BlogMapper.selectAllPosts", resultHandler);
    assertEquals(5, resultHandler.count);
  }

  private static class TestResultStopHandler implements ResultHandler {
    int count = 0;
    @Override
    public void handleResult(ResultContext context) {
      count++;
      if (count == 2) context.stop();
    }
  }

  @Test
  void shouldStopResultHandler() {
    final TestResultStopHandler resultHandler = new TestResultStopHandler();
    session.select("org.apache.ibatis.domain.blog.mappers.BlogMapper.selectAllPosts", null, resultHandler);
    assertEquals(2, resultHandler.count);
  }

  @Test
  void shouldOffsetAndLimitResultsUsingMapperClass() {
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    List<Map> posts = mapper.selectAllPosts(new RowBounds(2, 3));
    assertEquals(3, posts.size());
    assertEquals(3, posts.get(0).get("ID"));
    assertEquals(4, posts.get(1).get("ID"));
    assertEquals(5, posts.get(2).get("ID"));
  }

  private final String statement = "org.apache.ibatis.domain.blog.mappers.PostMapper.findPost";

  @Test
  void shouldFindPostsAllPostsWithDynamicSql() {
    List<Post> posts = session.selectList(statement);
    assertEquals(5, posts.size());
  }

  @Test
  void shouldFindPostByIDWithDynamicSql() {
    Map<String,Integer> map = new HashMap<>();
    map.put("id",1);
    List<Post> posts = session.selectList(statement,map);
    assertEquals(1, posts.size());
  }

  @Test
  void shouldFindPostsInSetOfIDsWithDynamicSql() {
    List<Integer> list = Arrays.asList(1,2,3);
    Map<String,List<Integer>> map = new HashMap<>();
    map.put("ids",list);
    List<Post> posts = session.selectList(statement,map);
    assertEquals(3, posts.size());
  }

  @Test
  void shouldFindPostsWithBlogIdUsingDynamicSql() {
    Map<String,Integer> map = new HashMap<>();
    map.put("blog_id",1);
    List<Post> posts = session.selectList(statement,map);
    assertEquals(2, posts.size());
  }

  @Test
  void shouldFindPostsWithAuthorIdUsingDynamicSql() {
    Map<String,Integer> map = new HashMap<>();
    map.put("author_id",101);
    List<Post> posts = session.selectList(statement,map);
    assertEquals(3, posts.size());
  }

  @Test
  void shouldFindPostsWithAuthorAndBlogIdUsingDynamicSql() {
    List<Integer> temp = Arrays.asList(0,1,2,3);
    Map<String,Object> map = new HashMap<>();
    map.put("ids",temp);
    map.put("blog_id", 1);
    List<Post> posts = session.selectList(statement,map);
    assertEquals(2, posts.size());
  }

  @Test
  void shouldFindPostsInList() {
    List<Integer> temp = Arrays.asList(0,1,2,3);
    List<Post> posts = session.selectList("org.apache.ibatis.domain.blog.mappers.PostMapper.selectPostIn", temp);
    assertEquals(3, posts.size());
  }

  List<Integer> temp = Arrays.asList(0,1,2,3,4);

  @Test
  void shouldFindOddPostsInList() {
    List<Post> posts = session.selectList("org.apache.ibatis.domain.blog.mappers.PostMapper.selectOddPostsIn",temp );
    // we're getting odd indexes, not odd values, 0 is not odd
    assertEquals(2, posts.size());
    assertEquals(1, posts.get(0).getId());
    assertEquals(3, posts.get(1).getId());
  }

  @Test
  void shouldSelectOddPostsInKeysList() {
    Map<String,List<Integer>> map = new HashMap<>();
    map.put("keys",temp);
    List<Post> posts = session.selectList("org.apache.ibatis.domain.blog.mappers.PostMapper.selectOddPostsInKeysList",map );
    // we're getting odd indexes, not odd values, 0 is not odd
    assertEquals(2, posts.size());
    assertEquals(1, posts.get(0).getId());
    assertEquals(3, posts.get(1).getId());
  }

}
