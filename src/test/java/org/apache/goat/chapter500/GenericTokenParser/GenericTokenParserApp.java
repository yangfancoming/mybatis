package org.apache.goat.chapter500.GenericTokenParser;

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
    map.put("first_name", "Goat");
    TokenHandler tokenHandler = new VariableTokenHandler(map);
    GenericTokenParser parser = new GenericTokenParser("${", "}",tokenHandler);
    String parse = parser.parse("111${first_name}222");
    System.out.println(parse);
  }


}
