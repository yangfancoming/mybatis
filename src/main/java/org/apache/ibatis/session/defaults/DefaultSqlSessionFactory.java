
package org.apache.ibatis.session.defaults;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

/**
 这么多的openSession重载方法，都是通过传入不同的参数构造SqlSession实例，
 有通过设置事务是否自动提交"autoCommit"，有设置执行器类型"ExecutorType"来构造的，还有事务的隔离级别等等。
 最后一个方法就告诉我们可以通过SqlSessionFactory来获取Configuration对象。
*/
public class DefaultSqlSessionFactory implements SqlSessionFactory {

  private final Configuration configuration;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   SqlSession中的策略模式体现在：创建数据源有3种方式，POOL、UNPOOL和JNDI。
   在创建Sqlsession的时候，是根据环境创建的，在Environment里面会指定数据源的方式，对于Sqlsession的使用代码来说，
   不管底层是如何创建Sqlsession的，都没有关系。只需要改配置，数据源模块就能够生产出3种不同类型的SqlSession。
   在上面的2个方法中，configuration.newExecutor(tx, execType)说明传到DefaultSqlSession的executor都是根据具体的类型execType创建的，
   我们看看Configuration#newExecutor()方法
  */


  /** 方式一：从数据源获取SqlSession
   核心方法，DefaultSqlSessionFactory其他方法都是调用此方法
   **/
  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      /**
       * 1.该方法先从configuration读取对应的环境配置
       * 通过Confuguration对象去获取Mybatis相关配置信息, Environment对象包含了数据源和事务的配置
       * 设置 搜索串：environmentsElement(root.evalNode("environments"));
      */
      final Environment environment = configuration.getEnvironment();
      /**
       * 2.初始化TransactionFactory
       * 通过环境配置获取事务工厂，如果没有配置默认是 new ManagedTransactionFactory();
      */
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      // 3.获得一个Transaction对象 (从数据源DataSource获取tx，这是和方式二最大的区别，其他的都差不多)
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      /**
       * 4.通过Transaction获取一个Executor对象
       * 通过配置创建一个Executor，Executor是对jdbc中Statement的封装
       * Executor 入口
      */
      final Executor executor = configuration.newExecutor(tx, execType);
      /**
       此处也是写死的，创建一个DefaultSqlSession对象
       从此处可以看出 DefaultSqlSession是SqlSession的实例。
       5.最后通过configuration、Executor、是否autoCommit三个参数构建了SqlSession
       在这里其实也可以看到端倪，SqlSession的执行，其实是委托给对应的Executor来进行的
      */
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()  //可能已经获取了一个连接，所以此处调用close事务
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  /**
   * 方式二：从数据库连接获取SqlSession
   * */
  private SqlSession openSessionFromConnection(ExecutorType execType, Connection connection) {
    try {
      boolean autoCommit; //1.确认是否自动提交
      try {
        autoCommit = connection.getAutoCommit();
      } catch (SQLException e) {
        // Failover to true, as most poor drivers or databases won't support transactions
        autoCommit = true;
      }
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      //2.从一个连接connection获取tx，这是和方式一最大的区别，其他的都差不多
      final Transaction tx = transactionFactory.newTransaction(connection);
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
    // 如果全局配置文件中 没有配置 <environments> 或是没有配置<transactionManager> 则使用默认的
    if (environment == null || environment.getTransactionFactory() == null) {
      return new ManagedTransactionFactory();
    }
    return environment.getTransactionFactory();
  }

  private void closeTransaction(Transaction tx) {
    if (tx != null) {
      try {
        tx.close();
      } catch (SQLException ignore) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }


  /*-------------------------------- 方式一：从数据源获取SqlSession-------------------------------*/

  @Override
  public SqlSession openSession() {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
  }

  @Override
  public SqlSession openSession(boolean autoCommit) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, autoCommit);
  }

  @Override
  public SqlSession openSession(TransactionIsolationLevel level) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), level, false);
  }

  @Override
  public SqlSession openSession(ExecutorType execType) {
    return openSessionFromDataSource(execType, null, false);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    return openSessionFromDataSource(execType, null, autoCommit);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
    return openSessionFromDataSource(execType, level, false);
  }

  /*--------------------------------方式二：从数据库连接获取SqlSession-------------------------------*/

  @Override
  public SqlSession openSession(Connection connection) {
    return openSessionFromConnection(configuration.getDefaultExecutorType(), connection);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, Connection connection) {
    return openSessionFromConnection(execType, connection);
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

}
