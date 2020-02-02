
package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * Creates an {@link SqlSession} out of a connection or a DataSource
 * 简单工厂模式(Simple Factory Pattern)：又称为静态工厂方法(Static Factory Method)模式
 * 可以看到，该Factory的openSession方法重载了很多个，分别支持autoCommit、Executor、Transaction等参数的输入，来构建核心的SqlSession对象
 * 工厂模式优点：解耦(降低类之间的依赖关系)
 */
public interface SqlSessionFactory {

  // 事务默认自动提交
  SqlSession openSession();
  // 可传入 事务是否自动提交
  SqlSession openSession(boolean autoCommit);
  // 可传入 数据库连接
  SqlSession openSession(Connection connection);
  // 可传入 事务隔离级别
  SqlSession openSession(TransactionIsolationLevel level);

  SqlSession openSession(ExecutorType execType);

  SqlSession openSession(ExecutorType execType, boolean autoCommit);

  SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);

  SqlSession openSession(ExecutorType execType, Connection connection);

  Configuration getConfiguration();

}
