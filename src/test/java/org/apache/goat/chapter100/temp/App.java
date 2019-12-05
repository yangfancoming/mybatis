package org.apache.goat.chapter100.temp;

import org.apache.ibatis.reflection.Reflector;
import org.junit.jupiter.api.Test;

/**
 * Created by Administrator on 2019/12/5.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/12/5---20:07
 */
public class App {

  @Test
  public void test(){
    Reflector reflector = new Reflector(Student.class);
    System.out.println(reflector);
  }
}
