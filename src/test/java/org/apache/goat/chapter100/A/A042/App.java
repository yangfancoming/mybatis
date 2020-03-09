package org.apache.goat.chapter100.A.A042;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.apache.goat.common.model.Zoo;
import org.junit.jupiter.api.Test;


/**
 * 推荐：
 *       比较重要的，复杂的Dao接口我们来写sql映射文件
 *       不重要的，简单的Dao接口为了开发快速可以使用注解；
 */
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A042/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**
   *        第二种方式， （class ）
   *        通过class指定接口，进而将接口与对应的xml文件形成映射关系
   *        不过，使用这种方式必须保证
   *        1.接口与mapper文件同名(不区分大小写)
   *        2.接口类和xml文件必须在同一个目录下
   *        我这儿接口是UserDao,那么意味着mapper文件为UserDao.xml
   *        <mapper class="com.dy.dao.UserDao"/>
   */
  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("org.apache.goat.chapter100.A.A042.FooMapper.selectById",1);
    System.out.println(foo);
  }


  @Test
  void test() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  /**
   * 第二种方式：（class ） 使用mapper注解  去掉局部 xml
   * 可以看到 该方式 没有 ZooMapper.xml 局部xml
   * 注解在 接口上 写了sql
   */
  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    ZooMapper zooMapper = sqlSession.getMapper(ZooMapper.class);
    Zoo zoo = zooMapper.selectById(1);
    System.out.println(zoo);
  }


}
