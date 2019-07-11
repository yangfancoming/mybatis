
package org.apache.ibatis.submitted.cache;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.googlecode.catchexception.apis.BDDCatchException.*;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * 和缓存有关的设置/属性：
 * 			1）、cacheEnabled=true：false：关闭缓存（二级缓存关闭）(一级缓存一直可用的)
 * 			2）、每个select标签都有useCache="true"：使用二级缓存
 * 					false：不使用缓存（一级缓存依然使用，二级缓存不使用）
 * 			3）、【每个增删改标签默认：flushCache="true"：（一级二级都会清除）】
 * 					增删改执行完成后就会清楚缓存； 测试：flushCache="true"：（一级二级都会清除）
 * 					查询标签默认：flushCache="false"：如果flushCache=true;每次查询之后都会清空缓存；缓存是没有被使用的；
 * 			4）、sqlSession.clearCache();只是清楚当前session的一级缓存；
*/
// issue #524
class CacheTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/cache/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/cache/CreateDB.sql");

  }

  /**
   * 一级缓存 测试   基于 BaseExecutor  类
   *
   * Mybatis的一级缓存。只需要在Mybatis的配置文件中，添加如下语句，就可以使用一级缓存。共有两个选项，SESSION或者STATEMENT，默认是SESSION级别。
   *  <setting name="localCacheScope" value="SESSION"/>
   *
   * 一级缓存：（本地缓存）：sqlSession级别的缓存。一级缓存是一直开启的，程序员无法将其关闭！ SqlSession级别的一个Map
   * 		与数据库同一次会话期间查询到的数据会放在本地缓存中。
   * 		以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库
   *
   * 		一级缓存失效情况（没有使用到当前一级缓存的情况，效果就是，还需要再向数据库发出查询）：
   * 		1、sqlSession不同。
   * 		2、sqlSession相同，查询条件不同.(当前一级缓存中还没有这个数据)
   * 		3、sqlSession相同，两次查询之间执行了增删改操作(增删改操作会清除一级缓存)
   * 		4、sqlSession相同，手动清除了一级缓存（缓存清空）  sqlSession.clearCache();
  */
  @Test
  void test() {
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    PersonMapper pm1 = sqlSession1.getMapper(PersonMapper.class);
    List<Person> all1 = pm1.findAll();
    System.out.println(all1);
    //第二次查询，由于是同一个sqlSession,会在缓存中查找查询结果 如果有，则直接从缓存中取出来，不和数据库进行交互
    List<Person> all2 = pm1.findAll();
    System.out.println(all2);
  }

  @Test
  void test1() {
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    PersonMapper pm1 = sqlSession1.getMapper(PersonMapper.class);
    List<Person> all1 = pm1.findAll();
    pm1.deleteById(1);
    System.out.println(all1);
    //第二次查询，由于 执行 insert | update | delete 语句，调用 doUpdate 方法实现,在执行这些语句的时候，会清空缓存  所以会走数据库
    List<Person> all2 = pm1.findAll();
    System.out.println(all2);
  }

  /**  二级缓存 测试   基于 CachingExecutor  类
   * 二级缓存：（全局缓存）：基于namespace级别的缓存：一个namespace对应一个二级缓存： <mapper namespace="org.apache.ibatis.submitted.cache.FooMapper">
   * 		工作机制：
   * 		1、一个会话，查询一条数据，这个数据就会被放在当前会话的一级缓存中；
   * 		2、如果会话关闭；一级缓存中的数据会被保存到二级缓存中；新的会话查询信息，就可以参照二级缓存中的内容；
   * 		3、sqlSession===EmployeeMapper==>Employee
   * 						DepartmentMapper===>Department
   * 			不同namespace查出的数据会放在自己对应的map缓存中
   * 			效果：数据会从二级缓存中获取 查出的数据都会被默认先放在一级缓存中。 只有会话提交或者关闭以后，一级缓存中的数据才会转移到二级缓存中
   *
   * 		使用步骤：
   * 			1）、开启全局二级缓存配置：<setting name="cacheEnabled" value="true"/>
   * 			2）、去mapper.xml中配置使用二级缓存： <cache></cache> 或 <cache/>
   * 			3）、我们的 实体类 需要实现序列化接口
   */

  @Test
  void test2() {
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper1 = sqlSession1.getMapper(FooMapper.class);
    List<Foo> all = fooMapper1.findAll();
    System.out.println(all); // 查库

    SqlSession sqlSession2 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper2 = sqlSession2.getMapper(FooMapper.class);
    List<Foo> all2 = fooMapper2.findAll();
    System.out.println(all2); // 查库
  }

  @Test
  void test3() {
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper1 = sqlSession1.getMapper(FooMapper.class);
    List<Foo> all = fooMapper1.findAll(); // 查库
    System.out.println(all);
    sqlSession1.close(); // //这里执行关闭操作，将 sqlSession1 中的数据写到二级缓存区域 因为如果会话关闭；一级缓存中的数据会被保存到二级缓存中

    SqlSession sqlSession2 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper2 = sqlSession2.getMapper(FooMapper.class);
    List<Foo> all2 = fooMapper2.findAll(); // 走缓存
    System.out.println(all2);
  }

  @Test
  void test4() {
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper1 = sqlSession1.getMapper(FooMapper.class);
    List<Foo> all = fooMapper1.findAll(); // 查库
    System.out.println(all);
    sqlSession1.close(); // //这里执行关闭操作，将 sqlSession1 中的数据写到二级缓存区域

    SqlSession sqlSession3 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper3 = sqlSession3.getMapper(FooMapper.class);
    int i = fooMapper3.deleteById(1);
    System.out.println(i);
    //执行提交，清空UserMapper下边的二级缓存
    sqlSession3.commit();
    sqlSession3.close();

    SqlSession sqlSession2 = sqlSessionFactory.openSession(false);
    FooMapper fooMapper2 = sqlSession2.getMapper(FooMapper.class);
    List<Foo> all2 = fooMapper2.findAll(); // 查库
    System.out.println(all2);
  }

  /*
   * Test Plan:
   *  1) SqlSession 1 executes "select * from A".
   *  2) SqlSession 1 closes.
   *  3) SqlSession 2 executes "delete from A where id = 1"
   *  4) SqlSession 2 executes "select * from A"
   *
   * Assert:  doit 没看懂  就是开启了二级缓存  也没有报错啊？？？
   *   Step 4 returns 1 row. (This case fails when caching is enabled.)
   */
  @Test
  void testplan1() {

    try (SqlSession sqlSession1 = sqlSessionFactory.openSession(false)) {
      PersonMapper pm = sqlSession1.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }
    try (SqlSession sqlSession2 = sqlSessionFactory.openSession(false)) {
      try {
        PersonMapper pm = sqlSession2.getMapper(PersonMapper.class);
        pm.deleteById(1);
        Assertions.assertEquals(1, pm.findAll().size());
      } finally {
        sqlSession2.commit(); // 提交事务后 删除数据库中记录
      }
    }
  }

  /*
   * Test Plan:
   *  1) SqlSession 1 executes "select * from A".
   *  2) SqlSession 1 closes.
   *  3) SqlSession 2 executes "delete from A where id = 1"
   *  4) SqlSession 2 executes "select * from A"
   *  5) SqlSession 2 rollback
   *  6) SqlSession 3 executes "select * from A"
   *
   * Assert:
   *   Step 6 returns 2 rows.
   */
  @Test
  void testplan2() {
    try (SqlSession sqlSession1 = sqlSessionFactory.openSession(false)) {
      PersonMapper pm = sqlSession1.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }

    try (SqlSession sqlSession2 = sqlSessionFactory.openSession(false)) {
      try {
        PersonMapper pm = sqlSession2.getMapper(PersonMapper.class);
        pm.deleteById(1);
      } finally {
        sqlSession2.rollback();
      }
    }

    try (SqlSession sqlSession3 = sqlSessionFactory.openSession(false)) {
      PersonMapper pm = sqlSession3.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }
  }

  /*
   * Test Plan with Autocommit on:
   *  1) SqlSession 1 executes "select * from A".
   *  2) SqlSession 1 closes.
   *  3) SqlSession 2 executes "delete from A where id = 1"
   *  4) SqlSession 2 closes.
   *  5) SqlSession 2 executes "select * from A".
   *  6) SqlSession 3 closes.
   *
   * Assert:
   *   Step 6 returns 1 row.
   */
  @Test
  void testplan3() {
    try (SqlSession sqlSession1 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession1.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }


    try (SqlSession sqlSession2 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession2.getMapper(PersonMapper.class);
      pm.deleteById(1);
    }

    try (SqlSession sqlSession3 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession3.getMapper(PersonMapper.class);
      Assertions.assertEquals(1, pm.findAll().size());
    }
  }

  /*-
   * Test case for #405
   *
   * Test Plan with Autocommit on:
   *  1) SqlSession 1 executes "select * from A".
   *  2) SqlSession 1 closes.
   *  3) SqlSession 2 executes "insert into person (id, firstname, lastname) values (3, hello, world)"
   *  4) SqlSession 2 closes.
   *  5) SqlSession 3 executes "select * from A".
   *  6) SqlSession 3 closes.
   *
   * Assert:
   *   Step 5 returns 3 row.
   */
  @Test
  void shouldInsertWithOptionsFlushesCache() {
    try (SqlSession sqlSession1 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession1.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }

    try (SqlSession sqlSession2 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession2.getMapper(PersonMapper.class);
      Person p = new Person(3, "hello", "world");
      pm.createWithOptions(p);
    }

    try (SqlSession sqlSession3 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession3.getMapper(PersonMapper.class);
      Assertions.assertEquals(3, pm.findAll().size());
    }
  }

  /*-
   * Test Plan with Autocommit on:
   *  1) SqlSession 1 executes select to cache result
   *  2) SqlSession 1 closes.
   *  3) SqlSession 2 executes insert without flushing cache
   *  4) SqlSession 2 closes.
   *  5) SqlSession 3 executes select (flushCache = false)
   *  6) SqlSession 3 closes.
   *  7) SqlSession 4 executes select (flushCache = true)
   *  8) SqlSession 4 closes.
   *
   * Assert:
   *   Step 5 returns 2 row.
   *   Step 7 returns 3 row.
   */
  @Test
  void shouldApplyFlushCacheOptions() {
    try (SqlSession sqlSession1 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession1.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }

    try (SqlSession sqlSession2 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession2.getMapper(PersonMapper.class);
      Person p = new Person(3, "hello", "world");
      pm.createWithoutFlushCache(p);
    }

    try (SqlSession sqlSession3 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession3.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }

    try (SqlSession sqlSession4 = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession4.getMapper(PersonMapper.class);
      Assertions.assertEquals(3, pm.findWithFlushCache().size());
    }
  }

  @Test
  void shouldApplyCacheNamespaceRef() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
      Person p = new Person(3, "hello", "world");
      pm.createWithoutFlushCache(p);
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession.getMapper(PersonMapper.class);
      Assertions.assertEquals(2, pm.findAll().size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
      ImportantPersonMapper pm = sqlSession.getMapper(ImportantPersonMapper.class);
      Assertions.assertEquals(3, pm.findWithFlushCache().size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession.getMapper(PersonMapper.class);
      Assertions.assertEquals(3, pm.findAll().size());
      Person p = new Person(4, "foo", "bar");
      pm.createWithoutFlushCache(p);
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
      SpecialPersonMapper pm = sqlSession.getMapper(SpecialPersonMapper.class);
      Assertions.assertEquals(4, pm.findWithFlushCache().size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
      PersonMapper pm = sqlSession.getMapper(PersonMapper.class);
      Assertions.assertEquals(4, pm.findAll().size());
    }
  }

  @Test
  void shouldApplyCustomCacheProperties() {
    CustomCache customCache = unwrap(sqlSessionFactory.getConfiguration().getCache(CustomCacheMapper.class.getName()));
    Assertions.assertEquals("bar", customCache.getStringValue());
    Assertions.assertEquals(1, customCache.getIntegerValue().intValue());
    Assertions.assertEquals(2, customCache.getIntValue());
    Assertions.assertEquals(3, customCache.getLongWrapperValue().longValue());
    Assertions.assertEquals(4, customCache.getLongValue());
    Assertions.assertEquals(5, customCache.getShortWrapperValue().shortValue());
    Assertions.assertEquals(6, customCache.getShortValue());
    Assertions.assertEquals((float) 7.1, customCache.getFloatWrapperValue(), 1);
    Assertions.assertEquals((float) 8.1, customCache.getFloatValue(), 1);
    Assertions.assertEquals(9.01, customCache.getDoubleWrapperValue(), 1);
    Assertions.assertEquals(10.01, customCache.getDoubleValue(), 1);
    Assertions.assertEquals((byte) 11, customCache.getByteWrapperValue().byteValue());
    Assertions.assertEquals((byte) 12, customCache.getByteValue());
    Assertions.assertTrue(customCache.getBooleanWrapperValue());
    Assertions.assertTrue(customCache.isBooleanValue());
  }

  @Test
  void shouldErrorUnsupportedProperties() {
    when(sqlSessionFactory.getConfiguration()).addMapper(CustomCacheUnsupportedPropertyMapper.class);
    then(caughtException()).isInstanceOf(CacheException.class)
      .hasMessage("Unsupported property type for cache: 'date' of type class java.util.Date");
  }

  @Test
  void shouldErrorInvalidCacheNamespaceRefAttributesSpecifyBoth() {
    when(sqlSessionFactory.getConfiguration().getMapperRegistry())
      .addMapper(InvalidCacheNamespaceRefBothMapper.class);
    then(caughtException()).isInstanceOf(BuilderException.class)
      .hasMessage("Cannot use both value() and name() attribute in the @CacheNamespaceRef");
  }

  @Test
  void shouldErrorInvalidCacheNamespaceRefAttributesIsEmpty() {
    when(sqlSessionFactory.getConfiguration().getMapperRegistry())
      .addMapper(InvalidCacheNamespaceRefEmptyMapper.class);
    then(caughtException()).isInstanceOf(BuilderException.class)
      .hasMessage("Should be specified either value() or name() attribute in the @CacheNamespaceRef");
  }

  private CustomCache unwrap(Cache cache){
    Field field;
    try {
      field = cache.getClass().getDeclaredField("delegate");
    } catch (NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
    try {
      field.setAccessible(true);
      return (CustomCache)field.get(cache);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } finally {
      field.setAccessible(false);
    }
  }

  @CacheNamespace(implementation = CustomCache.class, properties = {
      @Property(name = "date", value = "2016/11/21")
  })
  private interface CustomCacheUnsupportedPropertyMapper {
  }

  @CacheNamespaceRef(value = PersonMapper.class, name = "org.apache.ibatis.submitted.cache.PersonMapper")
  private interface InvalidCacheNamespaceRefBothMapper {
  }

  @CacheNamespaceRef
  private interface InvalidCacheNamespaceRefEmptyMapper {
  }

}
