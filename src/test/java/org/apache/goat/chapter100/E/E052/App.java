package org.apache.goat.chapter100.E.E052;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E052/mybatis-config.xml";


  @Test
  void insert1() throws Exception  {
    setUpByReader(XMLPATH);

  }

  @Test
  void insert2() throws Exception  {
    setUpByReader(XMLPATH);

  }

}
