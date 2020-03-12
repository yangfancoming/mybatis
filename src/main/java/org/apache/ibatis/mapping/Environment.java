
package org.apache.ibatis.mapping;

import javax.sql.DataSource;

import org.apache.ibatis.transaction.TransactionFactory;

/**
 * 全局配置文件中 具体指定的 项目运行环境
*/
public final class Environment {

  // 主标签   <environments default="dev_hsqldb"> 中的 dev_hsqldb
  private final String id;

  // 事务工厂接口实现类
  private final TransactionFactory transactionFactory;

  // 数据源
  private final DataSource dataSource;

  public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
    if (id == null)
      throw new IllegalArgumentException("Parameter 'id' must not be null");
    if (transactionFactory == null)
      throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
    if (dataSource == null)
      throw new IllegalArgumentException("Parameter 'dataSource' must not be null");
    this.id = id; // -modify
    this.transactionFactory = transactionFactory;
    this.dataSource = dataSource;
  }


  public String getId() {
    return this.id;
  }

  public TransactionFactory getTransactionFactory() {
    return this.transactionFactory;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * 一个静态内部类Builder（建造模式）
   * 用法应该是new Environment.Builder(id).transactionFactory(xx).dataSource(xx).build();
  */
  public static class Builder {
    private String id;
    private TransactionFactory transactionFactory;
    private DataSource dataSource;

    public Builder(String id) {
      this.id = id;
    }

    public Builder transactionFactory(TransactionFactory transactionFactory) {
      this.transactionFactory = transactionFactory;
      return this;
    }

    public Builder dataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      return this;
    }

    public String id() {
      return id;
    }

    public Environment build() {
      return new Environment(id, transactionFactory, dataSource);
    }

  }

}
