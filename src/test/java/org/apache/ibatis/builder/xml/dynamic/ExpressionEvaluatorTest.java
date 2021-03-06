
package org.apache.ibatis.builder.xml.dynamic;

import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

  private ExpressionEvaluator evaluator = new ExpressionEvaluator();

  Author author = new Author(1, "cbegin", "******", "cbegin@apache.org", "N/A", Section.NEWS);

  @Test
  void shouldCompareStringsReturnTrue() {
    boolean value = evaluator.evaluateBoolean("username == 'cbegin'", author);
    assertTrue(value);
  }

  @Test
  void shouldCompareStringsReturnFalse() {
    boolean value = evaluator.evaluateBoolean("username == 'norm'", author);
    assertFalse(value);
  }

  @Test
  void shouldReturnTrueIfNotNull() {
    boolean value = evaluator.evaluateBoolean("username", author);
    assertTrue(value);
  }

  @Test
  void shouldReturnFalseIfNull() {
    boolean value = evaluator.evaluateBoolean("password", new Author(1, "cbegin", null, "cbegin@apache.org", "N/A", Section.NEWS));
    assertFalse(value);
  }

  @Test
  void shouldReturnTrueIfNotZero() {
    boolean value = evaluator.evaluateBoolean("id", new Author(1, "cbegin", null, "cbegin@apache.org", "N/A", Section.NEWS));
    assertTrue(value);
  }

  @Test
  void shouldReturnFalseIfZero() {
    boolean value = evaluator.evaluateBoolean("id", new Author(0, "cbegin", null, "cbegin@apache.org", "N/A", Section.NEWS));
    assertFalse(value);
  }

  @Test
  void shouldReturnFalseIfZeroWithScale() {
    class Bean {
      @SuppressWarnings("unused")
      public double d = 0.0d;
    }
    assertFalse(evaluator.evaluateBoolean("d", new Bean()));
  }

  @Test
  void shouldIterateOverIterable() {
    final HashMap<String, String[]> parameterObject = new HashMap<String, String[]>() {{
      put("array", new String[]{"1", "2", "3"});
    }};
    final Iterable<?> iterable = evaluator.evaluateIterable("array", parameterObject);
    int i = 0;
    for (Object o : iterable) {
      assertEquals(String.valueOf(++i), o);
    }
  }


}
