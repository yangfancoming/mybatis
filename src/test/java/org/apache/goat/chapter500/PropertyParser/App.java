package org.apache.goat.chapter500.PropertyParser;

import org.junit.jupiter.api.Test;

import java.util.Properties;


public class App {


  @Test
  public void test() {
    Properties props = new Properties();
    props.setProperty("key", "value");
    String parse = PropertyParser.parse("${key}", props);
    String parse1 = PropertyParser.parse("${key:aaaa}", props);
    System.out.println(parse);
    System.out.println(parse1);

  }

}
