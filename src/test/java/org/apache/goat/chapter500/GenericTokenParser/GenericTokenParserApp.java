package org.apache.goat.chapter500.GenericTokenParser;

import org.apache.ibatis.parsing.GenericTokenParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2019/11/14.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/14---9:47
 */
public class GenericTokenParserApp {

  @Test
  public void test() {
    Map<String,String> map = new HashMap<>();
    map.put("first_name", "James");
    map.put("initial", "T");
    map.put("last_name", "Kirk");
    map.put("var{with}brace", "Hiya");
    map.put("", "");

    GenericTokenParser parser = new GenericTokenParser("${", "}", new VariableTokenHandler(map));
    String parse = parser.parse("${first_name} ${initial} ${last_name} reporting.");
    System.out.println(parse);
  }
}
