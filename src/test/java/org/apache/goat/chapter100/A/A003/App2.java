package org.apache.goat.chapter100.A.A003;

import org.apache.common.MyBaseJavaMapperConfig;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;


class App2 extends MyBaseJavaMapperConfig {

  public static final String XMLPATH_LOCAL = "org/apache/goat/chapter100/A/A003/local1.xml";

  @Test
  public void notNull() throws IOException {
    XMLMapperBuilder mapperBuilder = getMapperBuilder(configuration, XMLPATH_LOCAL);
    Assert.assertNotNull(mapperBuilder);

    mapperBuilder.bindMapperForNamespace("org.apache.goat.chapter100.A.A003.FooMapper");
  }

  @Test
  public void test() {

  }
}
