package org.apache.goat.chapter800;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2020/7/20.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/7/20---8:53
 */
public class AppTemp {

  @Test
  public void test1(){
    BigDecimal temp1 = new BigDecimal(100);
    BigDecimal temp2 = new BigDecimal(60);
    BigDecimal subtract = temp1.subtract(temp2);
    System.out.println(subtract);

  }
}
