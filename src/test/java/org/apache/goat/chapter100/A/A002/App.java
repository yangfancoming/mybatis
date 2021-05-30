package org.apache.goat.chapter100.A.A002;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;


class App extends MyBaseDataTest {

  public static final String XMLPATH1 = "org/apache/goat/chapter100/A/A002/mybatis-config.xml";
  public static final String XMLPATH2 = "org/apache/goat/chapter100/A/A002/mybatis-config2.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**
   *  mapper接口配合 全局xml + 局部xml  入门示例
  */
  @Test
  void selectById() throws Exception  {
    setUpByReader(XMLPATH1,DBSQL);
    FooMapper1 fooMapper = sqlSession.getMapper(FooMapper1.class);
    Foo foo = fooMapper.selectById(1);
    Assert.assertEquals(1,foo.getId());
  }

  /**
   *  mapper接口配合 全局xml + 注解 入门示例
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
   */
  @Test
  void gaga() throws Exception {
    // 配置
    DataSource dataSource = BaseDataTest.createGoatDataSource(DBSQL,"org/apache/common/goatdb.properties");
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("NoCare",transactionFactory , dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(FooMapper2.class);
    // 使用
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    sqlSession = sqlSessionFactory.openSession(autoCommit);
    FooMapper2 blogMapper = sqlSession.getMapper(FooMapper2.class);
    Foo foo = blogMapper.selectById(1);
    Assert.assertEquals(1,foo.getId());
  }
}
