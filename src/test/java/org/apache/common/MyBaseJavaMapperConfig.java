package org.apache.common;

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

  public MappedStatement getMappedStatement(String resource,String statementId) throws IOException {
    Configuration configuration = getConfiguration(resource);
    MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
    return mappedStatement;
  }

  public Configuration getConfiguration(String resource) throws IOException {
    Configuration configuration = new Configuration();
    InputStream inputStream = Resources.getResourceAsStream(resource);
    XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
    builder.parse();
    return configuration;
  }
}
