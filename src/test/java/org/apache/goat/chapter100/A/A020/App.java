package org.apache.goat.chapter100.A.A020;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;


/**
 * 源码搜索串： private void propertiesElement(XNode context)
*/
public class App extends MyBaseDataTest {

  /**
   *  <properties> 标签从外部配置文件读取属性值
   * 读取的是  resources/dbconfig.properties 路径下 属性文件
   */
  @Test
  void test1() throws Exception {
    setUpByInputStreamNoOpen("org/apache/goat/chapter100/A/A020/properties1.xml");
  }

  /**
   *  <properties> 标签从内部配置文件读取属性值
   * 读取的是  <properties> 下的子标签
   *     <property name="jdbc.driver" value="bar"/>
   *     <property name="jdbc.url" value="foo"/>
   *     <property name="jdbc.username" value="bar"/>
   *     <property name="jdbc.password" value="foo"/>
   */
  @Test
  void test2() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A020/properties2.xml");
  }

  /**
   *  <properties> 标签从外部配置文件读取属性值
   * 读取的是  resources/dbconfig.properties 路径下 属性文件
   *
   * 如果属性在不只一个地方进行了配置，那么 MyBatis 将按照下面的顺序来加载：
   * 1.在 properties 元素体内指定的属性首先被读取。
   * 2.然后根据 properties 元素中的 resource 属性读取类路径下属性文件或根据 url 属性指定的路径读取属性文件，并覆盖已读取的同名属性。
   * 3.最后读取作为方法参数传递的属性，并覆盖已读取的同名属性。
   * 因此，通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的是 properties 属性中指定的属性。
   */
  @Test
  void test3() throws Exception {
    setUpByInputStreamNoOpen("org/apache/goat/chapter100/A/A020/properties3.xml");
  }
}
