package org.apache.goat.chapter100.A.A020;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.junit.jupiter.api.Test;


/**
 * 源码位置： <properties> 标签 配置的三种情况列举：
 * @see XMLConfigBuilder#propertiesElement(org.apache.ibatis.parsing.XNode)
*/
public class App extends MyBaseDataTest {

  /**
   * 第一种情况： 只指定了外部配置文件
   *  <properties> 标签从外部配置文件读取属性值
   * 读取的是  resources/dbconfig.properties 路径下 属性文件
   */
  @Test
  void test1() throws Exception {
    setUpByInputStreamNoOpen("org/apache/goat/chapter100/A/A020/properties1.xml");
  }

  /**
   * 第二种情况： 只指定了 子标签属性   没有其他(外部配置文件)
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
   * 第三种情况： 同时指定 外部配置文件 和 子标签属性
   * 出现 优先级覆盖的情况
  */
  @Test
  void test3() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A020/properties3.xml");
  }

  /**
   *
   * 总体测试用例：
   *  <properties> 标签从外部配置文件读取属性值
   * 读取的是  resources/dbconfig.properties 路径下 属性文件
   *
   * 如果属性在多个地方进行了配置，那么 MyBatis 将按照下面的顺序来加载：
   * 1.在 properties 元素体内指定的属性首先被读取。
   * 2.然后根据 properties 元素中的 resource 属性读取类路径下属性文件或根据 url 属性指定的路径读取属性文件，并覆盖已读取的同名属性。
   * 3.最后读取作为方法参数传递的属性，并覆盖已读取的同名属性。
   * 因此，通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的是 properties 属性中指定的属性。
   *
   * sos  以上三种情况解析完成后，将保存到全局Configuration对象中，以后再任何需要使用的地方 可以使用
   * 比如  <environment id="development"> 标签中的  <property name="driver" value="${jdbc.driver}" /> 标签
   * 其中，${jdbc.driver} 就可以被 Configuration对象中的pros替换掉
   */
  @Test
  void test4() throws Exception {
    setUpByInputStreamNoOpen("org/apache/goat/chapter100/A/A020/properties4.xml");
  }
}
