
package org.apache.ibatis.mapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * Vendor DatabaseId provider.
 * It returns database product name as a databaseId.
 * If the user provides a properties it uses it to translate database product name
 * key="Microsoft SQL Server", value="ms" will return "ms".
 * It can return null, if no database product name or a properties was specified and no translation was found.
 */
public class VendorDatabaseIdProvider implements DatabaseIdProvider {

  private static final Log log = LogFactory.getLog(VendorDatabaseIdProvider.class);

  private Properties properties;

  private String getDatabaseName(DataSource dataSource) throws SQLException {
    String productName = getDatabaseProductName(dataSource); // MySQL
    if (properties != null) {
      for (Map.Entry<Object, Object> property : properties.entrySet()) {
        // 用key匹配 返回value
        if (productName.contains((String) property.getKey())) {
          return (String) property.getValue();
        }
      }
      return null; // no match, return null
    }
    return productName;
  }

  /**
   * 通过数据源获取数据库产品名称
   * @param dataSource  原生数据源
   * @return productName  数据库产品名称  eg： Oracle、MySQL、SQL Server、HSQL Database Engine
  */
  private String getDatabaseProductName(DataSource dataSource) throws SQLException {
    Connection con = null;
    try {
      // 1.连接数据库
      con = dataSource.getConnection();
      // 2.获取数据库元信息
      DatabaseMetaData metaData = con.getMetaData();
      // 3.获取数据库厂商标识
      return metaData.getDatabaseProductName();
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) { }
      }
    }
  }

  @Override
  public String getDatabaseId(DataSource dataSource) {
    if (dataSource == null) throw new NullPointerException("dataSource cannot be null");
    try {
      return getDatabaseName(dataSource);
    } catch (Exception e) {
      log.error("Could not get a databaseId from dataSource", e); // -modify
    }
    return null;
  }

  @Override
  public void setProperties(Properties p) {
    this.properties = p;
  }

}
