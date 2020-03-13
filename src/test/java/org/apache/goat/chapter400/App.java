package org.apache.goat.chapter400;

import org.apache.common.MyBaseJavaMapperConfig;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by Administrator on 2020/3/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/13---16:31
 */
public class App extends MyBaseJavaMapperConfig {

  String resource = "org/apache/goat/chapter400/Foo.xml";

  @Test
  public void test() throws IOException {
    Configuration configuration = getConfiguration(resource);
    System.out.println(configuration);
  }

  Integer dsa = null;

  // 当运行下面表达式时，会产生NullPointerException异常：
  @Test
  public void test1(){
//    Integer mark = (dsa == 1) ? 4 : dsa;
//    System.out.println(mark);
    System.out.println(Objects.nonNull(dsa)&& dsa == 1 ? 4 : dsa);
  }

  // 而在运行这个表达式时就正常：
  @Test
  public void test2(){
//    Integer mark = (dsa == 1) ? new Integer(4) : dsa;
//    System.out.println(mark);
    System.out.println(Objects.nonNull(dsa)&& dsa == 1 ? new Integer(4) : dsa);
  }

}
