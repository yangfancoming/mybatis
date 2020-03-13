package org.apache.goat.chapter100.G.G021;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.Foo;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by Administrator on 2020/2/7.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/2/7---14:33
 */
public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/G/G021/mybatis-config.xml";

  CacheLevel1Mapper cache1;
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

  @BeforeEach
  public void beforeEach() throws Exception {
    setUpByReader(XMLPATH);
    cache1 = sqlSession.getMapper(CacheLevel1Mapper.class);
  }

  // 验证一级缓存效果
  @Test
  public void test() {
    List<Foo> all1 = cache1.findAll(); // 查库
    System.out.println(all1);
    //第二次查询，由于是同一个sqlSession,会在缓存中查找查询结果 如果有，则直接从缓存中取出来，不和数据库进行交互
    List<Foo> all2 = cache1.findAll(); // 走缓存
    Assert.assertTrue(all1 == all2);
  }

  // 1、sqlSession不同。 导致的一级缓存无效
  @Test
  public void test1() throws Exception {
    SqlSession sqlSession1 = sqlSessionFactory.openSession();
    CacheLevel1Mapper pm1 = sqlSession1.getMapper(CacheLevel1Mapper.class);

    SqlSession sqlSession2 = sqlSessionFactory.openSession();
    CacheLevel1Mapper pm2 = sqlSession2.getMapper(CacheLevel1Mapper.class);

    List<Foo> all1 = pm1.findAll(); // 查库
    List<Foo> all2 = pm2.findAll(); // 查库
    Assert.assertFalse(all1 == all2);
  }

  // 2、sqlSession相同，查询条件不同.(当前一级缓存中还没有这个数据)
  @Test
  public void test2() {
    Foo foo1 = cache1.findById(1); // 查库
    Foo foo2 = cache1.findById(1); // 走缓存
    Assert.assertTrue(foo1 == foo2);
    Foo foo3 = cache1.findById(2); // 查库
    Assert.assertFalse(foo2 == foo3);
  }

  // 3、sqlSession相同，两次查询之间执行了增删改操作(增删改操作会清除一级缓存)
  @Test
  public void test3() {
    List<Foo> all1 = cache1.findAll(); // 查库
    List<Foo> all3 = cache1.findAll(); // 走缓存
    Assert.assertTrue(all1 == all3);
    cache1.deleteById(1); // 查库
    //第二次查询，由于 执行 insert | update | delete 语句，调用 doUpdate 方法实现,在执行这些语句的时候，会清空缓存  所以会走数据库
    List<Foo> all2 = cache1.findAll(); // 查库
    System.out.println(all2);
  }

  // 4、sqlSession相同，手动清除了一级缓存（缓存清空）  sqlSession.clearCache();
  @Test
  public void test4() {
    List<Foo> all1 = cache1.findAll();
    System.out.println(all1);// 查库
    sqlSession.clearCache(); // 清空一级缓存
    List<Foo> all2 = cache1.findAll(); // 查库
    Assert.assertFalse(all1 == all2);
  }

}
