package org.apache.goat.chapter100.B.B060;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;

import java.util.List;


/**  小结：
 * 1、接口式编程
 * 	原生：		Dao		====>  DaoImpl
 * 	mybatis：	Mapper	====>  xxMapper.xml
 *
 * 2、SqlSession代表和数据库的一次会话；用完必须关闭；
 * 3、SqlSession和connection一样她都是非线程安全,所有每次使用都应该去获取新的对象。
 * 4、mapper接口没有实现类，但是mybatis会为这个接口生成一个代理对象。（将接口和xml进行绑定）
 * 		EmployeeMapper empMapper =	sqlSession.getMapper(EmployeeMapper.class);
 * 5、两个重要的配置文件：
 * 		mybatis的全局配置文件：包含数据库连接池信息，事务管理器信息等...系统运行环境信息
 * 		sql 映射文件：保存了每一个sql语句的映射信息：将sql抽取出来。
 */

class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/B/B060/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**
   *  演示  mybatis 有 mapper 接口类的方式
   *  优点： 1.有参数安全类型检测  2.面向接口编程 (解耦)
   *
   * 加入了 mapper接口类 以后 必须要有两处绑定：
   1. 文件绑定：xml 中的 <mapper namespace="org.apache.ibatis.zgoat.A02.FooMapper">  必须指定 接口类全路径
   1. 方法绑定：xml 中的 statement id  必须与  mapper接口类中的 方法名  一致！
   */

  @Test
  void findAll() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    List<Foo> all = fooMapper.findAll();
    System.out.println(all);
  }

  @Test
  void deleteById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    int i = fooMapper.deleteById(2);
    System.out.println(i);
  }

  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  /**
   *  mapper 接口 与 xml 文件绑定后   其实就是 xml 作为 mapper接口的实现类
   *  只不过这个实现类  是 由mybatis 创建的代理实现类  （JDK动态代理）
  */
  @Test
  void test2() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    System.out.println(fooMapper.getClass()); // class com.sun.proxy.$Proxy13
  }



}
