package org.apache.goat.chapter500.PropertyParser;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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

  @Test
  public void test4() throws IOException {
    //创建数据报套接字对象，绑定端口号为6000
    DatagramSocket ds = new DatagramSocket(5002);
    //构建数据包接收数据：
    //创建字节数组
    byte[] buf = new byte[100];
    //创建数据包对象，它的长度不能超过数组的长度，我们把它设为100
    DatagramPacket dp = new DatagramPacket(buf, 100);
    //接收数据
    ds.receive(dp);
    //打印数据
    //getLength方法返回实际接收数；getData方法返回数据，返回格式为字节数组
    System.out.println(new String(buf, 0, dp.getLength()));
    //关闭数据报套接字
    ds.close();
  }
}
