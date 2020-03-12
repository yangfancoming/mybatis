package org.apache.goat.chapter100.A.A021;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;


public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A021/mybatis-config.xml";

  /**
   *  <properties> 标签从内部配置文件读取属性值
   * 读取的是  <properties> 下的子标签
   *     <property name="jdbc.driver" value="bar"/>
   *     <property name="jdbc.url" value="foo"/>
   *     <property name="jdbc.username" value="bar"/>
   *     <property name="jdbc.password" value="foo"/>
   */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH);
  }

}
