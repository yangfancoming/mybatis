package org.apache.goat.chapter700.A;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;


public class App  {


  /**
   * MapperProxy 源码解析
   *  源码位置搜索串：else if (method.isDefault())
  */
  @Test
  public void gaga() {
    UnpooledDataSource dataSource = new UnpooledDataSource();
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("No Care",transactionFactory , dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(MyMapper.class);

    // 使用
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    MyMapper mapper = sqlSession.getMapper(MyMapper.class);
    // 接口 default 方法
    int x = mapper.test(1);
    System.out.println(x);
  }

}
