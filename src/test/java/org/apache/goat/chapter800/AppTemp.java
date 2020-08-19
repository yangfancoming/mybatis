package org.apache.goat.chapter800;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    BigDecimal temp1 = new BigDecimal(0);
    BigDecimal temp2 = new BigDecimal(0);
    BigDecimal subtract = temp1.subtract(temp2);
    System.out.println(subtract);
  }

  @Test
  public void test2() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date dateStr = sdf.parse("1970-01-01");
    System.out.println(dateStr);
  }

  @Test
  public void test3() throws ParseException {
    Date dateStr = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String format = sdf.format(dateStr);
    System.out.println(format.length());
  }
}
