package org.apache.goat.chapter100.A.A002;

import org.apache.common.MyBaseDataTest;
import org.apache.common.mapper.BlogMapper;
import org.apache.goat.common.model.Foo;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.domain.blog.Blog;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A002/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  // 有mapper接口 入门示例
  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }

  /**  3、通过Configuration对象构建SqlSessionFactory
   *  挺长时间没碰 再次运行为啥又报错了？
   *  因为没有对应的 局部xml实现 或是没有@Select 注解实现
   *  org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): org.apache.goat.chapter100.A.A002.FooMapper.selectById
   */
  @Test
  void gaga() throws Exception {
    // 配置
    DataSource dataSource = BaseDataTest.createBlogDataSource();
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("NoCare",transactionFactory , dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(BlogMapper.class);

    // 使用
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    sqlSession = sqlSessionFactory.openSession(autoCommit);
    BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
    Blog zoo = blogMapper.selectBlog(1);
    System.out.println(zoo);
  }

}
