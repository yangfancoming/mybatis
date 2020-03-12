package org.apache.goat.chapter100.A.A012;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A012/mybatis-config.xml";

  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH);
  }

}
