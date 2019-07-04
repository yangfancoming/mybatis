
package org.apache.ibatis.datasource.pooled;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * @author Clinton Begin
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

  public PooledDataSourceFactory() {
    this.dataSource = new PooledDataSource();
  }

}
