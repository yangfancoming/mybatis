
package org.apache.ibatis.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

class GenericTokenParserTest {

  @Test
  void shouldDemonstrateGenericTokenReplacement() {
    Map<String,String> map = new HashMap<>();
    map.put("first_name", "James");
    map.put("initial", "T");
    map.put("last_name", "Kirk");
    map.put("var{with}brace", "Hiya");
    map.put("", "");

    GenericTokenParser parser = new GenericTokenParser("${", "}", new VariableTokenHandler(map));

    assertEquals("James T Kirk reporting.", parser.parse("${first_name} ${initial} ${last_name} reporting."));
    assertEquals("Hello captain James T Kirk", parser.parse("Hello captain ${first_name} ${initial} ${last_name}"));
    assertEquals("James T Kirk", parser.parse("${first_name} ${initial} ${last_name}"));
    assertEquals("JamesTKirk", parser.parse("${first_name}${initial}${last_name}"));
    assertEquals("{}JamesTKirk", parser.parse("{}${first_name}${initial}${last_name}"));
    assertEquals("}JamesTKirk", parser.parse("}${first_name}${initial}${last_name}"));

    assertEquals("}James{{T}}Kirk", parser.parse("}${first_name}{{${initial}}}${last_name}"));
    assertEquals("}James}T{Kirk", parser.parse("}${first_name}}${initial}{${last_name}"));
    assertEquals("}James}T{Kirk", parser.parse("}${first_name}}${initial}{${last_name}"));
    assertEquals("}James}T{Kirk{{}}", parser.parse("}${first_name}}${initial}{${last_name}{{}}"));
    assertEquals("}James}T{Kirk{{}}", parser.parse("}${first_name}}${initial}{${last_name}{{}}${}"));

    assertEquals("{$$something}JamesTKirk", parser.parse("{$$something}${first_name}${initial}${last_name}"));
    assertEquals("${", parser.parse("${"));
    assertEquals("${\\}", parser.parse("${\\}"));
    assertEquals("Hiya", parser.parse("${var{with\\}brace}"));
    assertEquals("", parser.parse("${}"));
    assertEquals("}", parser.parse("}"));
    assertEquals("Hello ${ this is a test.", parser.parse("Hello ${ this is a test."));
    assertEquals("Hello } this is a test.", parser.parse("Hello } this is a test."));
    assertEquals("Hello } ${ this is a test.", parser.parse("Hello } ${ this is a test."));
  }

  @Test
  void shallNotInterpolateSkippedVaiables() {
    GenericTokenParser parser = new GenericTokenParser("${", "}", new VariableTokenHandler(new HashMap<>()));
    assertEquals("${skipped} variable", parser.parse("\\${skipped} variable"));
    assertEquals("This is a ${skipped} variable", parser.parse("This is a \\${skipped} variable"));
    assertEquals("null ${skipped} variable", parser.parse("${skipped} \\${skipped} variable"));
    assertEquals("The null is ${skipped} variable", parser.parse("The ${skipped} is \\${skipped} variable"));
  }

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
