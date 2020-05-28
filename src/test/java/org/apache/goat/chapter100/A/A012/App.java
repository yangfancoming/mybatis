package org.apache.goat.chapter100.A.A012;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.junit.jupiter.api.Test;

/**
 * 源码位置： <settings> 标签解析
 * @see XMLConfigBuilder#settingsAsProperties(org.apache.ibatis.parsing.XNode)
 */
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A012/mybatis-config.xml";

  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH);
  }

}
