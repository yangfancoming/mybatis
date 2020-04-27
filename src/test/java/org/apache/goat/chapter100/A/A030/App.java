package org.apache.goat.chapter100.A.A030;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;


/**
 * 源码搜索串：   private void typeAliasesElement(XNode parent)
*/
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A030/mybatis-config.xml";

  /**
   * 别名方式一：  为单个类 起别名
   * 就是 全局xml 中配置  <typeAlias type="org.apache.goat.common.Foo" alias="Foo"/> 后 ，局部xml 中就可以直接写成  resultType="Foo"
  */
  @Test
  void test1() throws Exception {
    setUpByReaderNoOpen(XMLPATH);
  }
}
