package org.apache.goat.chapter100.A.A034;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A032/mybatis-config.xml";

  /**
   * 别名方式三：   批量起别名
   */
  @Test
  void test1() throws Exception {
    setUpByReaderNoOpen(XMLPATH);
  }
}
