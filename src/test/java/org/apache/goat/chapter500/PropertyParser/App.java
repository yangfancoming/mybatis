package org.apache.goat.chapter500.PropertyParser;

import org.junit.jupiter.api.Test;

import java.util.Properties;


public class App {

  Properties props = new Properties();

  /**
   *
  */
  @Test
  public void test1() {
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("key", "value");
    String parse = PropertyParser.parse("${key}", props);
    System.out.println(parse);

  }

  @Test
  public void test2() {
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "false");
    props.setProperty("key", "value");
    String parse1 = PropertyParser.parse("${key1:aaaa}", props);
    System.out.println(parse1);
  }

  @Test
  public void test3() {
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("key", "value");
    String parse1 = PropertyParser.parse("${key1:aaaa}", props);
    System.out.println(parse1);
  }
}
