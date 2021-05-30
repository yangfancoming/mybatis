package org.apache.goat.chapter100.A.A002;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;


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

  public static final String XMLPATH1 = "org/apache/goat/chapter100/A/A002/mybatis-config.xml";
  public static final String XMLPATH2 = "org/apache/goat/chapter100/A/A002/mybatis-config2.xml";
  public static final String XMLPATH3 = "org/apache/goat/chapter100/A/A002/mybatis-config3.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**
   *  演示  mybatis 有 mapper 接口类的方式
   *  优点： 1.有参数安全类型检测  2.面向接口编程 (解耦)
   *
   * 加入了 mapper接口类 以后 必须要有两处绑定：
   1. 文件绑定：xml 中的 <mapper namespace="org.apache.ibatis.zgoat.A02.FooMapper">  必须指定 接口类全路径
   1. 方法绑定：xml 中的 statement id  必须与  mapper接口类中的 方法名  一致！
   *  mapper 接口 与 xml 文件绑定后   其实就是 xml 作为 mapper接口的实现类
   *  只不过这个实现类  是 由mybatis 创建的代理实现类  （JDK动态代理）
   */



  /**
   *  mapper接口配合 全局xml + 局部xml   完全依赖xml  入门示例
  */
  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH1,DBSQL);
    FooMapper1 fooMapper = sqlSession.getMapper(FooMapper1.class);
    Foo foo = fooMapper.selectById(1);
    Assert.assertEquals(1,foo.getId());
  }


  /**
   *  @Delete 注解和局部xml的<delete>标签不能同时出现,可以理解为一个接口方法不能有多个实现类
  */
  @Test
  void deleteById()  {
    Assertions.assertThrows(PersistenceException.class, ()->  setUpByReader(XMLPATH3,DBSQL));
  }

  /**
   *  mapper接口配合 全局xml + 注解 入门示例
   *  依靠全局xml中的    <mapper class="org.apache.goat.chapter100.A.A002.FooMapper2"/> 来注册mapper接口
   *  没有xml实现 依靠mapper接口中的方法上的注解实现sql
   */
  @Test
  void selectById2() throws Exception  {
    setUpByReader(XMLPATH2,DBSQL);
    FooMapper2 fooMapper = sqlSession.getMapper(FooMapper2.class);
    Foo foo = fooMapper.selectById(1);
    Assert.assertEquals(1,foo.getId());
  }

  /**
   * mapper接口配合 构建Configuration对象  入门示例
   * 不依靠任何xml  手动构建Configuration对象  手动注册mapper接口
   * 依靠mapper接口中的方法上的注解实现sql
   */
  @Test
  void gaga() throws Exception {
    // 配置
    DataSource dataSource = BaseDataTest.createGoatDataSource(DBSQL,"org/apache/common/goatdb.properties");
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("NoCare",transactionFactory , dataSource);
    // 手动构建Configuration对象
    Configuration configuration = new Configuration(environment);
    // 手动注册mapper接口
    configuration.addMapper(FooMapper2.class);
    // 使用
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    sqlSession = sqlSessionFactory.openSession(autoCommit);
    FooMapper2 blogMapper = sqlSession.getMapper(FooMapper2.class);
    Foo foo = blogMapper.selectById(1);
    Assert.assertEquals(1,foo.getId());
  }
}
