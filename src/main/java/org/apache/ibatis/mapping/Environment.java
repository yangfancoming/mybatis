
package org.apache.ibatis.mapping;

import javax.sql.DataSource;

import org.apache.ibatis.transaction.TransactionFactory;

/**  决定加载哪种环境(开发环境/生产环境)
 <environments default="development">
   <environment id="development">

     <transactionManager type="JDBC">
     <property name="" value="" />
     </transactionManager>

     <dataSource type="UNPOOLED">
       <property name="driver" value="org.hsqldb.jdbcDriver" />
       <property name="url" value="jdbc:hsqldb:mem:cache" />
       <property name="username" value="sa" />
     </dataSource>

   </environment>
 </environments>
*/

public final class Environment {
  //环境id
  private final String id;
  //事务工厂
  private final TransactionFactory transactionFactory;
  //数据源
  private final DataSource dataSource;

  public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
    if (id == null) {
      throw new IllegalArgumentException("Parameter 'id' must not be null");
    }
    if (transactionFactory == null) {
      throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
    }
    this.id = id;
    if (dataSource == null) {
      throw new IllegalArgumentException("Parameter 'dataSource' must not be null");
    }
    this.transactionFactory = transactionFactory;
    this.dataSource = dataSource;
  }
/**
   一个静态内部类Builder
   建造模式
   用法应该是new Environment.Builder(id).transactionFactory(xx).dataSource(xx).build();
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
      return this.id;
    }

    public Environment build() {
      return new Environment(this.id, this.transactionFactory, this.dataSource);
    }

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

}
