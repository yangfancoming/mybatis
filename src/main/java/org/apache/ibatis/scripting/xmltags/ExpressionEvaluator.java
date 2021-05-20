
package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.builder.BuilderException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**  表达式计算器 */
public class ExpressionEvaluator {

  public boolean evaluateBoolean(String expression, Object parameterObject) {
    // 用OGNL解析expression表达式
    Object value = OgnlCache.getValue(expression, parameterObject);
    // 处理Boolean类型
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    // 处理数字类型
    if (value instanceof Number) {
      return new BigDecimal(String.valueOf(value)).compareTo(BigDecimal.ZERO) != 0;
    }
    return value != null;
  }

  public Iterable<?> evaluateIterable(String expression, Object parameterObject) {
    // 使用OGNL来解析expression
    Object value = OgnlCache.getValue(expression, parameterObject);
    if (value == null) {
      throw new BuilderException("The expression '" + expression + "' evaluated to a null value.");
    }
    // 处理可迭代集合类型
    if (value instanceof Iterable) {
      return (Iterable<?>) value;
    }
    // 处理数组类型，转化为List,并返回该List
    if (value.getClass().isArray()) {
      // the array may be primitive, so Arrays.asList() may throw
      // a ClassCastException (issue 209).  Do the work manually
      // Curse primitives! :) (JGB)
      int size = Array.getLength(value);
      List<Object> answer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        Object o = Array.get(value, i);
        answer.add(o);
      }
      return answer;
    }
    // 处理Map类型，返回Map的entrySet集合（Set）
    if (value instanceof Map) {
      return ((Map) value).entrySet();
    }
    throw new BuilderException("Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
  }

}
