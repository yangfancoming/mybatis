
package org.apache.goat.chapter100.G.G025;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.model.Foo;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 和缓存有关的设置/属性：
 * 			1）、cacheEnabled=true：false：关闭缓存（二级缓存关闭）(一级缓存一直可用的)
 * 			2）、每个select标签都有useCache="true"：使用二级缓存  false：不使用缓存（一级缓存依然使用，二级缓存不使用）
 * 			3）、【每个增删改标签默认：flushCache="true"：（一级二级都会清除）】
 * 					增删改执行完成后就会清楚缓存； 测试：flushCache="true"：（一级二级都会清除）
 * 					查询标签默认：flushCache="false"：如果flushCache=true;每次查询之后都会清空缓存；缓存是没有被使用的；
 * 			4）、sqlSession.clearCache();只是清除当前session的一级缓存；
*/
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/G/G025/mybatis-config.xml";

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
  public void test2() throws Exception {
    setUpByReader(XMLPATH);
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper1 = sqlSession1.getMapper(CacheLevel2Mapper.class);
    List<Foo> all = fooMapper1.findAll();
    System.out.println(all); // 查库

    SqlSession sqlSession2 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper2 = sqlSession2.getMapper(CacheLevel2Mapper.class);
    List<Foo> all2 = fooMapper2.findAll();
    System.out.println(all2); // 查库
  }

  @Test
  public void test3() throws Exception {
    setUpByReader(XMLPATH);
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper1 = sqlSession1.getMapper(CacheLevel2Mapper.class);
    List<Foo> all = fooMapper1.findAll(); // 查库
    System.out.println(all);
    sqlSession1.close(); // //这里执行关闭操作，将 sqlSession1 中的数据写到二级缓存区域 因为如果会话关闭；一级缓存中的数据会被保存到二级缓存中

    SqlSession sqlSession2 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper2 = sqlSession2.getMapper(CacheLevel2Mapper.class);
    List<Foo> all2 = fooMapper2.findAll(); // 从二级缓存中拿数据    DEBUG:Cache Hit Ratio [org.apache.goat.chapter100.G.G025.CacheLevel2Mapper]: 0.5
    System.out.println(all2);
  }

  @Test
  public void test4() throws Exception {
    setUpByReader(XMLPATH);
    SqlSession sqlSession1 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper1 = sqlSession1.getMapper(CacheLevel2Mapper.class);
    List<Foo> all = fooMapper1.findAll(); // 查库
    System.out.println(all);
    sqlSession1.close(); // //这里执行关闭操作，将 sqlSession1 中的数据写到二级缓存区域

    SqlSession sqlSession3 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper3 = sqlSession3.getMapper(CacheLevel2Mapper.class);
    int i = fooMapper3.deleteById(1);
    System.out.println(i);
    //执行提交，清空UserMapper下边的二级缓存
    sqlSession3.commit();
    sqlSession3.close();

    SqlSession sqlSession2 = sqlSessionFactory.openSession(false);
    CacheLevel2Mapper fooMapper2 = sqlSession2.getMapper(CacheLevel2Mapper.class);
    List<Foo> all2 = fooMapper2.findAll(); // 查库
    System.out.println(all2);
  }

}
