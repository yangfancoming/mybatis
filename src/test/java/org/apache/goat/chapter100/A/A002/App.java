package org.apache.goat.chapter100.A.A002;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.apache.ibatis.BaseDataTest;
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

  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH,DBSQL);
    FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = fooMapper.selectById(1);
    System.out.println(foo);
  }


  /**  3、通过Configuration对象构建SqlSessionFactory */
  @Test
  void gaga() throws Exception {
    //    DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
    DataSource dataSource = BaseDataTest.createBlogDataSource();
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("development", transactionFactory, dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(FooMapper.class);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    sqlSession = sqlSessionFactory.openSession(autoCommit);
    FooMapper blogMapper = sqlSession.getMapper(FooMapper.class);
    Foo foo = blogMapper.selectById(1);
    System.out.println(foo);
  }


}
