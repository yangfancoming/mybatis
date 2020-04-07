package org.apache.goat.chapter700.A;

import org.apache.common.MyBaseJavaConfig;
import org.junit.jupiter.api.Test;


public class App extends MyBaseJavaConfig {


  /**
   * MapperProxy 源码解析
   *  源码位置搜索串：else if (method.isDefault())
  */
  @Test
  public void defaultTest() {
    DefaultMapper mapper = getMapper(DefaultMapper.class);
    // 接口 default 方法
    System.out.println(mapper.test(1));
  }

  @Test
  public void defaultT1est() {
    ObjectMapper mapper = getMapper(ObjectMapper.class);
    // Object 方法
    System.out.println(mapper.hashCode());
  }
}
