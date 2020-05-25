package org.apache.goat.chapter100.A.A030;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.junit.jupiter.api.Test;


/**
 * 源码位置
 * @see XMLConfigBuilder#typeAliasesElement(org.apache.ibatis.parsing.XNode)
*/
class App extends MyBaseDataTest {

  /**
   * 别名方式一：  为单个类 起别名
   * 就是 全局xml 中配置  <typeAlias type="org.apache.goat.common.Foo" alias="Foo"/> 后 ，局部xml 中就可以直接写成  resultType="Foo"
  */
  @Test
  void test1() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias1.xml");
  }

  /**
   * 别名方式三：   批量起别名
   */
  @Test
  void test2() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias3.xml");
  }
}
