
package org.apache.ibatis.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 Executor分成两大类，一类是CacheExecutor 另一类是普通Executor
 普通类又分为：
 ExecutorType.SIMPLE: 这个执行器类型不做特殊的事情。它为每个语句的执行创建一个新的预处理语句。每执行一次update或select，就开启一个Statement对象，用完立刻关闭Statement对象。
 ExecutorType.REUSE: 这个执行器类型会复用预处理语句。 执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，而是放置于Map<String, Statement>内，供下一次使用。
 ExecutorType.BATCH: 这个执行器会批量执行所有更新语句,如果 SELECT 在它们中间执行还会标定它们是 必须的,来保证一个简单并易于理解的行为。
                     执行update（没有select，JDBC批处理不支持select），将所有sql都添加到批处理中（addBatch()），等待统一执行（executeBatch()），
                     它缓存了多个Statement对象，每个Statement对象都是addBatch()完毕后，等待逐一执行executeBatch()批处理的；BatchExecutor相当于维护了多个桶，
                     每个桶里都装了很多属于自己的SQL，就像苹果蓝里装了很多苹果，番茄蓝里装了很多番茄，最后，再统一倒进仓库

 分别对应SimpleExecutor，ReuseExecutor，BatchExecutor，他们都继承于BaseExecutor，BatchExecutor专门用于执行批量sql操作，
 ReuseExecutor会重用statement执行sql操作，SimpleExecutor只是简单执行sql没有什么特别的。
*/
public class SimpleExecutor extends BaseExecutor {

  public SimpleExecutor(Configuration configuration, Transaction transaction) {
    super(configuration, transaction);
  }

  @Override
  public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();// 获得配置
      // 获得statementHandler里面有statement，来处理
      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
      stmt = prepareStatement(handler, ms.getStatementLog());
      return handler.update(stmt);// 最终是一个statement进行处理
    } finally {
      closeStatement(stmt);
    }
  }

  /**
   * 查询的实现  真正的doQuery操作是由SimplyExecutor代理来完成的，该方法中有2个子流程，一个是SQL参数的设置，另一个是SQL查询操作和结果集的封装
   * */
  @Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
      // 1.创建StatementHandler的代理类  根据既有的参数，创建StatementHandler对象来执行查询操作
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
      // 2.用StatementHandler对象创建stmt,并使用StatementHandler对占位符进行处理  创建java.Sql.Statement对象，传递给StatementHandler对象
      /* 子流程1: SQL查询参数的设置 */
      stmt = prepareStatement(handler, ms.getStatementLog());
      // 3.通过statementHandler对象调用ResultSetHandler将结果集转化为指定对象返回
      /* 子流程2: SQL查询操作和结果集封装 */
      return handler.query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }

  @Override
  protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
    Configuration configuration = ms.getConfiguration();
    StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
    Statement stmt = prepareStatement(handler, ms.getStatementLog());
    stmt.closeOnCompletion();
    return handler.queryCursor(stmt);
  }

  @Override
  public List<BatchResult> doFlushStatements(boolean isRollback) {
    return Collections.emptyList();
  }

  /**
   * 1.创建Statement
   * 2.首先获取数据库connection连接，
   * 3.然后准备statement，然后就设置SQL查询中的参数值。
   * 4.打开一个connection连接，在使用完后不会close，而是存储下来，当下次需要打开连接时就直接返回。
   * */
  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    /* 获取Connection连接 */// 1.获取connection对象的动态代理，添加日志能力；(这里参考日志模块的代理模式) // 使用底层的 jdbc 的代码 获取数据库连接  // NOTE: 获取数据库连接
    Connection connection = getConnection(statementLog);
    /* 准备Statement */  // 2.使用StatementHandler，利用connection创建（prepare）Statement // NOTE: 创建Statement
    // doit 这里为什么会进入到 org.apache.ibatis.plugin.Plugin.invoke 方法中？？？
    Statement stmt = handler.prepare(connection, transaction.getTimeout());
    /* 准备Statement */  // 3.使用StatementHandler处理占位符  // NOTE: 参数设置
    // @Signature(type= StatementHandler.class,method="parameterize",args=java.sql.Statement.class)
    handler.parameterize(stmt);
    return stmt;
  }

}
