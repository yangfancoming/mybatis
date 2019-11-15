package org.apache.goat.chapter500.PropertyParser;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Properties;


public class App {

  Properties props = new Properties();

  /**
   * 开启默认值模式
  */
  @Test
  public void test1() {
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("key", "value");
    String parse = PropertyParser.parse("${key}", props);
    Assert.assertEquals("value" ,parse);
  }

  /**
   * 关闭默认值模式
   */
  @Test
  public void test2() {
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "false");
    props.setProperty("key", "value");
    String parse = PropertyParser.parse("${key1:aaaa}", props);
    Assert.assertEquals("${key1:aaaa}" ,parse);
  }

  /**
   * 开启默认值模式
   */
  @Test
  public void test3() {
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("key", "value");
    String parse = PropertyParser.parse("${key1:aaaa}", props);
    Assert.assertEquals("aaaa" ,parse);
  }
}
