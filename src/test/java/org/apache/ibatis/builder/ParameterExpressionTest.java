
package org.apache.ibatis.builder;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParameterExpressionTest {

  @Test
  void simpleProperty() {
    Map<String, String> result = new ParameterExpression("id");
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("id", result.get("property"));
  }

  @Test
  void propertyWithSpacesInside() {
    Map<String, String> result = new ParameterExpression(" with spaces ");
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("with spaces", result.get("property"));
  }

  @Test
  void simplePropertyWithOldStyleJdbcType() {
    Map<String, String> result = new ParameterExpression("id:VARCHAR");
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("id", result.get("property"));
    Assertions.assertEquals("VARCHAR", result.get("jdbcType"));
  }

  @Test
  void oldStyleJdbcTypeWithExtraWhitespaces() {
    Map<String, String> result = new ParameterExpression(" id :  VARCHAR ");
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("id", result.get("property"));
    Assertions.assertEquals("VARCHAR", result.get("jdbcType"));
  }

  @Test
  void expressionWithOldStyleJdbcType() {
    Map<String, String> result = new ParameterExpression("(id.toString()):VARCHAR");
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("id.toString()", result.get("expression"));
    Assertions.assertEquals("VARCHAR", result.get("jdbcType"));
  }

  @Test
  void simplePropertyWithOneAttribute() {
    Map<String, String> result = new ParameterExpression("id,name=value");
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("id", result.get("property"));
    Assertions.assertEquals("value", result.get("name"));
  }

  @Test
  void expressionWithOneAttribute() {
    Map<String, String> result = new ParameterExpression("(id.toString()),name=value");
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("id.toString()", result.get("expression"));
    Assertions.assertEquals("value", result.get("name"));
  }

  @Test
  void simplePropertyWithManyAttributes() {
    Map<String, String> result = new ParameterExpression("id, attr1=val1, attr2=val2, attr3=val3");
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("id", result.get("property"));
    Assertions.assertEquals("val1", result.get("attr1"));
    Assertions.assertEquals("val2", result.get("attr2"));
    Assertions.assertEquals("val3", result.get("attr3"));
  }

  @Test
  void expressionWithManyAttributes() {
    Map<String, String> result = new ParameterExpression("(id.toString()), attr1=val1, attr2=val2, attr3=val3");
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("id.toString()", result.get("expression"));
    Assertions.assertEquals("val1", result.get("attr1"));
    Assertions.assertEquals("val2", result.get("attr2"));
    Assertions.assertEquals("val3", result.get("attr3"));
  }

  @Test
  void simplePropertyWithOldStyleJdbcTypeAndAttributes() {
    Map<String, String> result = new ParameterExpression("id:VARCHAR, attr1=val1, attr2=val2");
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("id", result.get("property"));
    Assertions.assertEquals("VARCHAR", result.get("jdbcType"));
    Assertions.assertEquals("val1", result.get("attr1"));
    Assertions.assertEquals("val2", result.get("attr2"));
  }

  @Test
  void simplePropertyWithSpaceAndManyAttributes() {
    Map<String, String> result = new ParameterExpression("user name, attr1=val1, attr2=val2, attr3=val3");
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("user name", result.get("property"));
    Assertions.assertEquals("val1", result.get("attr1"));
    Assertions.assertEquals("val2", result.get("attr2"));
    Assertions.assertEquals("val3", result.get("attr3"));
  }

  @Test
  void shouldIgnoreLeadingAndTrailingSpaces() {
    Map<String, String> result = new ParameterExpression(" id , jdbcType =  VARCHAR,  attr1 = val1 ,  attr2 = val2 ");
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("id", result.get("property"));
    Assertions.assertEquals("VARCHAR", result.get("jdbcType"));
    Assertions.assertEquals("val1", result.get("attr1"));
    Assertions.assertEquals("val2", result.get("attr2"));
  }

  @Test
  void invalidOldJdbcTypeFormat() {
    try {
      new ParameterExpression("id:");
      Assertions.fail();
    } catch (BuilderException e) {
      Assertions.assertTrue(e.getMessage().contains("Parsing error in {id:} in position 3"));
    }
  }

  @Test
  void invalidJdbcTypeOptUsingExpression() {
    try {
      new ParameterExpression("(expression)+");
      Assertions.fail();
    } catch (BuilderException e) {
      Assertions.assertTrue(e.getMessage().contains("Parsing error in {(expression)+} in position 12"));
    }
  }

}
