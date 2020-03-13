package org.apache.common;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * Created by Administrator on 2020/3/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/13---13:57
 */
public abstract class MyBaseJavaConfig {

  public static <T> T  getMapper(Class<T> T ){
    // 先配置
    UnpooledDataSource dataSource = new UnpooledDataSource();
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("No Care",transactionFactory , dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(T);
    // 再使用
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    return sqlSession.getMapper(T);
  }
}
