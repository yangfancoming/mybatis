package org.apache.goat.chapter100.E.E066;

import org.apache.goat.MyBaseDataTest;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E066/mybatis-config.xml";

  @Test
  void deleteById1() throws Exception  {
    setUpByReader(XMLPATH);


  }



}
