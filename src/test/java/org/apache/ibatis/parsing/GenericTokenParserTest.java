
package org.apache.ibatis.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericTokenParserTest {

  @Disabled("Because it randomly fails on Travis CI. It could be useful during development.")
  @Test
  void shouldParseFastOnJdk7u6() {
    Assertions.assertTimeout(Duration.ofMillis(1), () -> {
      Map<String,String> map = new HashMap<>();
      map.put("first_name", "James");
      map.put("initial", "T");
      map.put("last_name", "Kirk");
      map.put("", "");

      // issue #760
      GenericTokenParser parser = new GenericTokenParser("${", "}", new VariableTokenHandler(map));

      StringBuilder input = new StringBuilder();
      for (int i = 0; i < 10000; i++) {
        input.append("${first_name} ${initial} ${last_name} reporting. ");
      }
      StringBuilder expected = new StringBuilder();
      for (int i = 0; i < 10000; i++) {
        expected.append("James T Kirk reporting. ");
      }
      assertEquals(expected.toString(), parser.parse(input.toString()));
    });
  }

}
