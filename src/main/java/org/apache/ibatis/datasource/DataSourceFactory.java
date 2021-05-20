
package org.apache.ibatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;


public interface DataSourceFactory {

  // 设置 DataSource 的相关属性，一般在初始化完成后进行设置
  void setProperties(Properties props);

  // 获取数据源 DataSource 对象
  DataSource getDataSource();
}
