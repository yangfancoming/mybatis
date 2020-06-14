package org.apache.goat.chapter100.A.A030;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;


/**
 * 源码位置
 * @see XMLConfigBuilder#typeAliasesElement(org.apache.ibatis.parsing.XNode)
*/
class App extends MyBaseDataTest {


  /**
   * 别名方式一：  为单个类 起别名   （显示指定别名的情况）
   * 就是 全局xml 中配置  <typeAlias type="org.apache.goat.common.Foo" alias="Foo"/> 后 ，局部xml 中就可以直接写成  resultType="Foo"
  */
  @Test
  void test11() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias1-1.xml");
    Configuration config = sqlSessionFactory.getConfiguration();
    TypeAliasRegistry typeAliasRegistry = config.getTypeAliasRegistry();
    Map<String, Class<?>> typeAliases = typeAliasRegistry.getTypeAliases();
    // 别名都将转换为小写
    Assert.assertTrue(typeAliases.containsKey("foo0"));
    Assert.assertFalse(typeAliases.containsKey("Foo0"));
  }

  /**
   * 别名方式一：  为单个类 起别名  （不显示指定别名的情况） 默认将类名转为小写
   * 就是 全局xml 中配置  <typeAlias type="org.apache.goat.common.Foo" /> 后 ，局部xml 中就可以直接写成  resultType="Foo"
   */
  @Test
  void test12() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias1-2.xml");
    Configuration config = sqlSessionFactory.getConfiguration();
    TypeAliasRegistry typeAliasRegistry = config.getTypeAliasRegistry();
    Map<String, Class<?>> typeAliases = typeAliasRegistry.getTypeAliases();
    // 别名都将转换为小写
    Assert.assertTrue(typeAliases.containsKey("foo"));
    Assert.assertFalse(typeAliases.containsKey("Foo"));
  }

  /**
   * 别名方式二：   批量起别名
   */
  @Test
  void test2() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias2.xml");

  }

  /**
   * 别名方式三： 注解起别名 （显示指定别名的情况） 将忽略注解指定的别名
   */
  @Test
  void test31() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias3-1.xml");
    Configuration config = sqlSessionFactory.getConfiguration();
    TypeAliasRegistry typeAliasRegistry = config.getTypeAliasRegistry();
    Map<String, Class<?>> typeAliases = typeAliasRegistry.getTypeAliases();
    Assert.assertTrue(typeAliases.containsKey("bar0000"));
    Assert.assertFalse(typeAliases.containsKey("Bar0000"));
  }

  /**
   * 别名方式三： 注解起别名 （不显示指定别名的情况）  将使用注解指定的别名
   */
  @Test
  void test32() throws Exception {
    setUpByReaderNoOpen("org/apache/goat/chapter100/A/A030/typeAlias3-2.xml");
    Configuration config = sqlSessionFactory.getConfiguration();
    TypeAliasRegistry typeAliasRegistry = config.getTypeAliasRegistry();
    Map<String, Class<?>> typeAliases = typeAliasRegistry.getTypeAliases();
    Assert.assertTrue(typeAliases.containsKey("what"));
  }
}
