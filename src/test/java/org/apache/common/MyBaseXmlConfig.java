package org.apache.common;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import java.io.IOException;
import java.io.InputStream;


public abstract class MyBaseXmlConfig {

  public XMLConfigBuilder getXMLConfigBuilder(String resource) throws IOException {
    InputStream inputStream = Resources.getResourceAsStream(resource);
    return new XMLConfigBuilder(inputStream);
  }

    // eg:  "org/apache/ibatis/builder/MinimalMapperConfig.xml";
    public Configuration getConfiguration(String resource) throws IOException {
      InputStream inputStream = Resources.getResourceAsStream(resource);
      XMLConfigBuilder builder = new XMLConfigBuilder(inputStream);
      return builder.parse();
    }

}
