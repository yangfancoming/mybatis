package org.apache.common;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2020/3/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/13---13:57
 */
public abstract class MyBaseJavaMapperConfig {

  public Configuration configuration = new Configuration();

  // 通过自定义的 Configuration 获取 MappedStatement
  public MappedStatement getMappedStatement(Configuration configuration,String localXml,String statementId) throws IOException {
    InputStream inputStream = Resources.getResourceAsStream(localXml);
    XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, localXml, configuration.getSqlFragments());
    builder.parse();
    MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
    return mappedStatement;
  }

  // 再空配置的Configuration基础上获取 MappedStatement
  public MappedStatement getMappedStatement(String localXml,String statementId) throws IOException {
    Configuration configuration = getConfiguration(localXml);
    MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
    return mappedStatement;
  }

  // 获取空配置的 Configuration
  public Configuration getConfiguration(String localXml) throws IOException {
    InputStream inputStream = Resources.getResourceAsStream(localXml);
    XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, localXml, configuration.getSqlFragments());
    builder.parse();
    return configuration;
  }
}
