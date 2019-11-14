package org.apache.goat.chapter500.XPathParser;

import org.apache.goat.chapter500.Resources;
import org.apache.ibatis.parsing.XPathParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

/**
 * Created by Administrator on 2019/11/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/13---19:52
 */
public class XPathParserApp {

  private String resource = "org/apache/goat/chapter500/myTest.xml";

  private XPathParser parser;

  @Test
  public void test() throws Exception{
    InputStream inputStream = Resources.getResourceAsStream(resource);
    System.out.println(inputStream);
  }

  @Test
  public void test2() throws Exception{
    parser = new XPathParser( Resources.getResourceAsStream(resource), false, null, null);
    Long aLong = parser.evalLong("/employee/birth_date/year");
    Short aShort = parser.evalShort("/employee/birth_date/month");
    Integer integer = parser.evalInteger("/employee/birth_date/day");
    System.out.println(aLong);
    System.out.println(aShort);
    System.out.println(integer);
  }
}
