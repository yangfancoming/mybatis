
package org.apache.ibatis.binding;

import static com.googlecode.catchexception.apis.BDDCatchException.*;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import javassist.util.proxy.Proxy;

import javax.sql.DataSource;

import net.sf.cglib.proxy.Factory;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Blog;
import org.apache.ibatis.domain.blog.DraftPost;
import org.apache.ibatis.domain.blog.Post;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.*;

class BindingTest {

  private static SqlSessionFactory sqlSessionFactory;
  SqlSession session = sqlSessionFactory.openSession();
  BoundBlogMapper blogMapper = session.getMapper(BoundBlogMapper.class);
  BoundAuthorMapper authorMapper = session.getMapper(BoundAuthorMapper.class);
  Author author = new Author(-1, "cbegin", "******", "cbegin@nowhere.com", "N/A", Section.NEWS);

  @BeforeAll
  static void setup() throws Exception {
    DataSource dataSource = BaseDataTest.createBlogDataSource();
    BaseDataTest.runScript(dataSource, BaseDataTest.BLOG_DDL);
    BaseDataTest.runScript(dataSource, BaseDataTest.BLOG_DATA);
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("Production", transactionFactory, dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.setLazyLoadingEnabled(true);
    configuration.setUseActualParamName(false); // to test legacy style reference (#{0} #{1})   测试旧样式引用
    configuration.getTypeAliasRegistry().registerAlias(Blog.class);
    configuration.getTypeAliasRegistry().registerAlias(Post.class);
    configuration.getTypeAliasRegistry().registerAlias(Author.class);
    configuration.addMapper(BoundBlogMapper.class);
    configuration.addMapper(BoundAuthorMapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
  }

  @AfterEach
  public void after(){
    session.rollback();
  }

  /* 主要涉及 mapper.xml 中 resultMap 标签级联多级对象嵌套的 映射规则  */
  @Test
  void shouldSelectBlogWithPostsUsingSubSelect() {
    Blog b = blogMapper.selectBlogWithPostsUsingSubSelect(1);
    assertEquals(1, b.getId());
    assertNotNull(b.getAuthor());
    assertEquals(101, b.getAuthor().getId());
    assertEquals("jim", b.getAuthor().getUsername());
    assertEquals("********", b.getAuthor().getPassword());
    assertEquals(2, b.getPosts().size());
  }

  /**  测试点：  List<Integer> 作为参数 传递
   [2019-07-09 15:55:12,494]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:126)DEBUG:==>  Preparing: select * from post where id in (?,?,?)
   [2019-07-09 15:55:12,540]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:126)DEBUG:==> Parameters: 3(Integer), 1(Integer), 5(Integer)
   [2019-07-09 15:55:12,573]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:126)DEBUG:<==      Total: 3
   */
  @Test
  void shouldFindPostsInList() {
    List<Integer> integers = Arrays.asList(3,1,5);
    List<Post> posts = authorMapper.findPostsInList(integers);
    assertEquals(3, posts.size());
  }

  /**  测试点： Integer[] 作为参数 传递
   [2019-07-09 16:17:07,230]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:126)DEBUG:==>  Preparing: select * from post where id in (?,?,?)
   [2019-07-09 16:17:07,317]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:126)DEBUG:==> Parameters: 1(Integer), 3(Integer), 5(Integer)
   [2019-07-09 16:17:07,347]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:126)DEBUG:<==      Total: 3
  */
  @Test
  void shouldFindPostsInArray() {
    Integer[] params = new Integer[]{1, 3, 5};
    List<Post> posts = authorMapper.findPostsInArray(params);
    assertEquals(3, posts.size());
  }

  /**  测试点：   select * from post where id in (#{one},#{two},#{2}) */
  @Test
  void shouldFindThreeSpecificPosts() {
    List<Post> posts = authorMapper.findThreeSpecificPosts(1, new RowBounds(1, 1), 3, 5);
    assertEquals(1, posts.size());
    assertEquals(3, posts.get(0).getId());
  }

  @Test
  void shouldInsertAuthorWithSelectKey() {
    int rows = authorMapper.insertAuthor(author);
    assertEquals(1, rows);
  }

  @Test
  void verifyErrorMessageFromSelectKey() {
    try {
      when(authorMapper).insertAuthorInvalidSelectKey(author);
      then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
        "### The error may exist in org/apache/ibatis/binding/BoundAuthorMapper.xml" + System.lineSeparator() +
          "### The error may involve org.apache.ibatis.binding.BoundAuthorMapper.insertAuthorInvalidSelectKey!selectKey" + System.lineSeparator() +
          "### The error occurred while executing a query");
    } finally { }
  }


  @Test
  void verifyErrorMessageFromInsertAfterSelectKey() {
    try {
      when(authorMapper).insertAuthorInvalidInsert(author);
      then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
        "### The error may exist in org/apache/ibatis/binding/BoundAuthorMapper.xml" + System.lineSeparator() +
          "### The error may involve org.apache.ibatis.binding.BoundAuthorMapper.insertAuthorInvalidInsert" + System.lineSeparator() +
          "### The error occurred while executing an update");
    } finally { }
  }

  @Test
  void shouldInsertAuthorWithSelectKeyAndDynamicParams() {
    int rows = authorMapper.insertAuthorDynamic(author);
    assertEquals(1, rows);
    assertNotEquals(-1, author.getId()); // id must be autogenerated
    Author author2 = authorMapper.selectAuthor(author.getId());
    assertNotNull(author2);
    assertEquals(author.getEmail(), author2.getEmail());
  }

  @Test
  void shouldSelectRandom() {
    Integer x = blogMapper.selectRandom();
    assertNotNull(x);
  }

  @Test
  void shouldExecuteBoundSelectListOfBlogsStatement() {
    List<Blog> blogs = blogMapper.selectBlogs();
    assertEquals(2, blogs.size());
  }

  @Test
  void shouldExecuteBoundSelectMapOfBlogsById() {
    Map<Integer,Blog> blogs = blogMapper.selectBlogsAsMapById();
    assertEquals(2, blogs.size());
    for(Map.Entry<Integer,Blog> blogEntry : blogs.entrySet()) {
      assertEquals(blogEntry.getKey(), (Integer) blogEntry.getValue().getId());
    }
  }

  @Test
  void shouldExecuteMultipleBoundSelectOfBlogsByIdInWithProvidedResultHandlerBetweenSessions() {
    final DefaultResultHandler handler = new DefaultResultHandler();
    session.select("selectBlogsAsMapById", handler);
    final DefaultResultHandler moreHandler = new DefaultResultHandler();
    session.select("selectBlogsAsMapById", moreHandler);
    assertEquals(2, handler.getResultList().size());
    assertEquals(2, moreHandler.getResultList().size());
  }

  @Test
  void shouldExecuteMultipleBoundSelectOfBlogsByIdInWithProvidedResultHandlerInSameSession() {
    final DefaultResultHandler handler = new DefaultResultHandler();
    session.select("selectBlogsAsMapById", handler);
    final DefaultResultHandler moreHandler = new DefaultResultHandler();
    session.select("selectBlogsAsMapById", moreHandler);
    assertEquals(2, handler.getResultList().size());
    assertEquals(2, moreHandler.getResultList().size());
  }

  @Test
  void shouldExecuteMultipleBoundSelectMapOfBlogsByIdInSameSessionWithoutClearingLocalCache() {
    Map<Integer,Blog> blogs = blogMapper.selectBlogsAsMapById();
    Map<Integer,Blog> moreBlogs = blogMapper.selectBlogsAsMapById();
    assertEquals(2, blogs.size());
    assertEquals(2, moreBlogs.size());
    for(Map.Entry<Integer,Blog> blogEntry : blogs.entrySet()) {
      assertEquals(blogEntry.getKey(), (Integer) blogEntry.getValue().getId());
    }
    for(Map.Entry<Integer,Blog> blogEntry : moreBlogs.entrySet()) {
      assertEquals(blogEntry.getKey(), (Integer) blogEntry.getValue().getId());
    }
  }

  @Test
  void shouldExecuteMultipleBoundSelectMapOfBlogsByIdBetweenTwoSessionsWithGlobalCacheEnabled() {
    Map<Integer,Blog> blogs = blogMapper.selectBlogsAsMapById();
    Map<Integer,Blog> moreBlogs = blogMapper.selectBlogsAsMapById();
    assertEquals(2, blogs.size());
    assertEquals(2, moreBlogs.size());
    for(Map.Entry<Integer,Blog> blogEntry : blogs.entrySet()) {
      assertEquals(blogEntry.getKey(), (Integer) blogEntry.getValue().getId());
    }
    for(Map.Entry<Integer,Blog> blogEntry : moreBlogs.entrySet()) {
      assertEquals(blogEntry.getKey(), (Integer) blogEntry.getValue().getId());
    }
  }

  @Test
  void shouldSelectListOfBlogsUsingXMLConfig() {
    List<Blog> blogs = blogMapper.selectBlogsFromXML();
    assertEquals(2, blogs.size());
  }

  @Test
  void shouldExecuteBoundSelectListOfBlogsStatementUsingProvider() {
    List<Blog> blogs = blogMapper.selectBlogsUsingProvider();
    assertEquals(2, blogs.size());
  }

  @Test
  void shouldExecuteBoundSelectListOfBlogsAsMaps() {
    List<Map<String,Object>> blogs = blogMapper.selectBlogsAsMaps();
    assertEquals(2, blogs.size());
  }

  @Test
  void shouldSelectListOfPostsLike() {
    List<Post> posts = blogMapper.selectPostsLike(new RowBounds(1,1),"%a%");
    assertEquals(1, posts.size());
  }

  @Test
  void shouldSelectListOfPostsLikeTwoParameters() {
    List<Post> posts = blogMapper.selectPostsLikeSubjectAndBody(new RowBounds(1,1),"%a%","%a%");
    assertEquals(1, posts.size());
  }

  @Test
  void shouldExecuteBoundSelectOneBlogStatement() {
    Blog blog = blogMapper.selectBlog(1);
    assertEquals(1, blog.getId());
    assertEquals("Jim Business", blog.getTitle());
  }

  @Test
  void shouldExecuteBoundSelectOneBlogStatementWithConstructor() {
    Blog blog = blogMapper.selectBlogUsingConstructor(1);
    assertEquals(1, blog.getId());
    assertEquals("Jim Business", blog.getTitle());
    assertNotNull(blog.getAuthor(), "author should not be null");
    List<Post> posts = blog.getPosts();
    assertTrue(posts != null && !posts.isEmpty(), "posts should not be empty");
  }

  @Test
  void shouldExecuteBoundSelectBlogUsingConstructorWithResultMap() {
    Blog blog = blogMapper.selectBlogUsingConstructorWithResultMap(1);
    assertEquals(1, blog.getId());
    assertEquals("Jim Business", blog.getTitle());
    assertNotNull(blog.getAuthor(), "author should not be null");
    List<Post> posts = blog.getPosts();
    assertTrue(posts != null && !posts.isEmpty(), "posts should not be empty");
  }

  @Test
  void shouldExecuteBoundSelectBlogUsingConstructorWithResultMapAndProperties() {
    Blog blog = blogMapper.selectBlogUsingConstructorWithResultMapAndProperties(1);
    assertEquals(1, blog.getId());
    assertEquals("Jim Business", blog.getTitle());
    assertNotNull(blog.getAuthor(), "author should not be null");
    Author author = blog.getAuthor();
    assertEquals(101, author.getId());
    assertEquals("jim@ibatis.apache.org", author.getEmail());
    assertEquals("jim", author.getUsername());
    assertEquals(Section.NEWS, author.getFavouriteSection());
    List<Post> posts = blog.getPosts();
    assertNotNull(posts, "posts should not be empty");
    assertEquals(2, posts.size());
  }

  @Disabled
  @Test // issue #480 and #101
  void shouldExecuteBoundSelectBlogUsingConstructorWithResultMapCollection() {
    Blog blog = blogMapper.selectBlogUsingConstructorWithResultMapCollection(1);
    assertEquals(1, blog.getId());
    assertEquals("Jim Business", blog.getTitle());
    assertNotNull(blog.getAuthor(), "author should not be null");
    List<Post> posts = blog.getPosts();
    assertTrue(posts != null && !posts.isEmpty(), "posts should not be empty");
  }

  @Test
  void shouldExecuteBoundSelectOneBlogStatementWithConstructorUsingXMLConfig() {
    Blog blog = blogMapper.selectBlogByIdUsingConstructor(1);
    assertEquals(1, blog.getId());
    assertEquals("Jim Business", blog.getTitle());
    assertNotNull(blog.getAuthor(), "author should not be null");
    List<Post> posts = blog.getPosts();
    assertTrue(posts != null && !posts.isEmpty(), "posts should not be empty");
  }

  @Test
  void shouldSelectOneBlogAsMap() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("id", 1);
    Map<String,Object> blog = blogMapper.selectBlogAsMap(map);
    assertEquals(1, blog.get("ID"));
    assertEquals("Jim Business", blog.get("TITLE"));
  }

  @Test
  void shouldSelectOneAuthor() {
    Author author = authorMapper.selectAuthor(101);
    assertEquals(101, author.getId());
    assertEquals("jim", author.getUsername());
    assertEquals("********", author.getPassword());
    assertEquals("jim@ibatis.apache.org", author.getEmail());
    assertEquals("", author.getBio());
  }

  @Test
  void shouldSelectOneAuthorFromCache() {
    Author author1 = selectOneAuthor();
    Author author2 = selectOneAuthor();
    assertSame(author1, author2, "Same (cached) instance should be returned unless rollback is called.");
  }

  private Author selectOneAuthor() {
    return authorMapper.selectAuthor(101);
  }

  @Test
  void shouldSelectOneAuthorByConstructor() {
    Author author = authorMapper.selectAuthorConstructor(101);
    assertEquals(101, author.getId());
    assertEquals("jim", author.getUsername());
    assertEquals("********", author.getPassword());
    assertEquals("jim@ibatis.apache.org", author.getEmail());
    assertEquals("", author.getBio());
  }

  @Test
  void shouldSelectDraftTypedPosts() {
    List<Post> posts = blogMapper.selectPosts();
    assertEquals(5, posts.size());
    assertTrue(posts.get(0) instanceof DraftPost);
    assertFalse(posts.get(1) instanceof DraftPost);
    assertTrue(posts.get(2) instanceof DraftPost);
    assertFalse(posts.get(3) instanceof DraftPost);
    assertFalse(posts.get(4) instanceof DraftPost);
  }

  @Test
  void shouldSelectDraftTypedPostsWithResultMap() {
    List<Post> posts = blogMapper.selectPostsWithResultMap();
    assertEquals(5, posts.size());
    assertTrue(posts.get(0) instanceof DraftPost);
    assertFalse(posts.get(1) instanceof DraftPost);
    assertTrue(posts.get(2) instanceof DraftPost);
    assertFalse(posts.get(3) instanceof DraftPost);
    assertFalse(posts.get(4) instanceof DraftPost);
  }

  @Test
  void shouldReturnANotNullToString() {
    assertNotNull(blogMapper.toString());
  }

  @Test
  void shouldReturnANotNullHashCode() {
    assertNotNull(blogMapper.hashCode());
  }

  @Test
  void shouldCompareTwoMappers() {
    BoundBlogMapper mapper2 = session.getMapper(BoundBlogMapper.class);
    assertNotEquals(blogMapper, mapper2);
  }

  @Test
  void shouldFailWhenSelectingOneBlogWithNonExistentParam() {
    assertThrows(Exception.class, () -> blogMapper.selectBlogByNonExistentParam(1));
  }

  @Test
  void shouldFailWhenSelectingOneBlogWithNullParam() {
    assertThrows(Exception.class, () -> blogMapper.selectBlogByNullParam(null));
  }

  @Test // Decided that maps are dynamic so no existent params do not fail
  void shouldFailWhenSelectingOneBlogWithNonExistentNestedParam() {
    blogMapper.selectBlogByNonExistentNestedParam(1, Collections.emptyMap());
  }

  @Test
  void shouldSelectBlogWithDefault30ParamNames() {
    Blog blog = blogMapper.selectBlogByDefault30ParamNames(1, "Jim Business");
    assertNotNull(blog);
  }

  /**
   @Select ("SELECT * FROM blog WHERE id = #{param1} AND title = #{param2}")
   Blog selectBlogByDefault31ParamNames(int id, String title);
  */
  @Test
  void shouldSelectBlogWithDefault31ParamNames() {
    Blog blog = blogMapper.selectBlogByDefault31ParamNames(1, "Jim Business");
    assertNotNull(blog);
  }

  /** 测试点  动态传入 列名
   @Select ("SELECT * FROM blog WHERE ${column} = #{id} AND title = #{value}")
   Blog selectBlogWithAParamNamedValue(@Param("column") String column, @Param("id") int id, @Param("value") String title);
  */
  @Test
  void shouldSelectBlogWithAParamNamedValue() {
    Blog blog = blogMapper.selectBlogWithAParamNamedValue("id", 1, "Jim Business");
    assertNotNull(blog);
  }

  @Test
  void shouldCacheMapperMethod() throws Exception {
    // Create another mapper instance with a method cache we can test against:
    final MapperProxyFactory<BoundBlogMapper> mapperProxyFactory = new MapperProxyFactory<>(BoundBlogMapper.class);
    assertEquals(BoundBlogMapper.class, mapperProxyFactory.getMapperInterface());
    final BoundBlogMapper mapper = mapperProxyFactory.newInstance(session);
    assertNotSame(mapper, mapperProxyFactory.newInstance(session));
    assertTrue(mapperProxyFactory.getMethodCache().isEmpty());

    // Mapper methods we will call later:
    final Method selectBlog = BoundBlogMapper.class.getMethod("selectBlog", Integer.TYPE);
    final Method selectBlogByIdUsingConstructor = BoundBlogMapper.class.getMethod("selectBlogByIdUsingConstructor", Integer.TYPE);

    // Call mapper method and verify it is cached:
    mapper.selectBlog(1);
    assertEquals(1, mapperProxyFactory.getMethodCache().size());
    assertTrue(mapperProxyFactory.getMethodCache().containsKey(selectBlog));
    final MapperMethod cachedSelectBlog = mapperProxyFactory.getMethodCache().get(selectBlog);

    // Call mapper method again and verify the cache is unchanged:
    session.clearCache();
    mapper.selectBlog(1);
    assertEquals(1, mapperProxyFactory.getMethodCache().size());
    assertSame(cachedSelectBlog, mapperProxyFactory.getMethodCache().get(selectBlog));

    // Call another mapper method and verify that it shows up in the cache as well:
    session.clearCache();
    mapper.selectBlogByIdUsingConstructor(1);
    assertEquals(2, mapperProxyFactory.getMethodCache().size());
    assertSame(cachedSelectBlog, mapperProxyFactory.getMethodCache().get(selectBlog));
    assertTrue(mapperProxyFactory.getMethodCache().containsKey(selectBlogByIdUsingConstructor));
  }

  @Test
  void shouldGetBlogsWithAuthorsAndPosts() {
    List<Blog> blogs = blogMapper.selectBlogsWithAutorAndPosts();
    assertEquals(2, blogs.size());
    assertTrue(blogs.get(0) instanceof Proxy);
    assertEquals(101, blogs.get(0).getAuthor().getId());
    assertEquals(1, blogs.get(0).getPosts().size());
    assertEquals(1, blogs.get(0).getPosts().get(0).getId());
    assertTrue(blogs.get(1) instanceof Proxy);
    assertEquals(102, blogs.get(1).getAuthor().getId());
    assertEquals(1, blogs.get(1).getPosts().size());
    assertEquals(2, blogs.get(1).getPosts().get(0).getId());
  }

  @Test
  void shouldGetBlogsWithAuthorsAndPostsEagerly() {
    List<Blog> blogs = blogMapper.selectBlogsWithAutorAndPostsEagerly();
    assertEquals(2, blogs.size());
    assertFalse(blogs.get(0) instanceof Factory);
    assertEquals(101, blogs.get(0).getAuthor().getId());
    assertEquals(1, blogs.get(0).getPosts().size());
    assertEquals(1, blogs.get(0).getPosts().get(0).getId());
    assertFalse(blogs.get(1) instanceof Factory);
    assertEquals(102, blogs.get(1).getAuthor().getId());
    assertEquals(1, blogs.get(1).getPosts().size());
    assertEquals(2, blogs.get(1).getPosts().get(0).getId());
  }

  @Test
  void executeWithResultHandlerAndRowBounds() {
    final DefaultResultHandler handler = new DefaultResultHandler();
    blogMapper.collectRangeBlogs(handler, new RowBounds(1, 1));
    assertEquals(1, handler.getResultList().size());
    Blog blog = (Blog) handler.getResultList().get(0);
    assertEquals(2, blog.getId());
  }

  /**
   @Select ({ "SELECT * FROM blog ORDER BY id"})
   @MapKey ("id")
   Map<Integer,Blog> selectRangeBlogsAsMapById(RowBounds rowBounds);
  */
  @Test
  void executeWithMapKeyAndRowBounds() {
    Map<Integer, Blog> blogs = blogMapper.selectRangeBlogsAsMapById(new RowBounds(1, 1));
    assertEquals(1, blogs.size());
    Blog blog = blogs.get(2);
    assertEquals(2, blog.getId());
  }


  @Test
  void executeWithCursorAndRowBounds() {
    try {
      try (Cursor<Blog> blogs = blogMapper.openRangeBlogs(new RowBounds(1, 1)) ) {
        Iterator<Blog> blogIterator = blogs.iterator();
        Blog blog = blogIterator.next();
        assertEquals(2, blog.getId());
        assertFalse(blogIterator.hasNext());
      }
    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  void registeredMappers() {
    Collection<Class<?>> mapperClasses = sqlSessionFactory.getConfiguration().getMapperRegistry().getMappers();
    assertEquals(2, mapperClasses.size());
    assertTrue(mapperClasses.contains(BoundBlogMapper.class));
    assertTrue(mapperClasses.contains(BoundAuthorMapper.class));
  }

}
