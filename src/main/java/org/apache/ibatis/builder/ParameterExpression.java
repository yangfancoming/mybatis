
package org.apache.ibatis.builder;

import java.util.HashMap;

/**
 * Inline parameter expression parser. Supported grammar (simplified):
 * inline-parameter = (propertyName | expression) oldJdbcType attributes
 * propertyName = /expression language's property navigation path/
 * expression = '(' /expression language's expression/ ')'
 * oldJdbcType = ':' /any valid jdbc type/
 * attributes = (',' attribute)*
 * attribute = name '=' value
 *
 例如：(id.toString()):VARCHAR, attr1=val1, attr2=val2, attr3=val3
 * 解析后结果是：
 * expression: id.toString()
 * jdbcType: VARCHAR
 * attr2:val2
 * attr1:val1
 * attr3:val3
 *
 * 例如2：
 * id:VARCHAR, attr1=val1, attr2=val2, attr3=val3
 * property: id
 * jdbcType: VARCHAR
 * attr2:val2
 * attr1:val1
 * attr3:val3
 */
public class ParameterExpression extends HashMap<String, String> {

  private static final long serialVersionUID = -2417552199605158680L;

  // -modify
  private static final Character JDBCTYPE = ':';
  private static final Character EXPRESSION = '(';
  private static final Character ATTRIBUTE = ',';

  public ParameterExpression(String expression) {
    parse(expression);
  }

  // 解析表达式入口  以括号确定
  private void parse(String expression) {
    int p = skipWS(expression, 0);
    if (expression.charAt(p) == EXPRESSION) {
      expression(expression, p + 1);
    } else {
      property(expression, p);
    }
  }

  // 解析表达式
  private void expression(String expression, int left) {
    int match = 1;
    int right = left + 1;
    while (match > 0) {
      if (expression.charAt(right) == ')') {
        match--;
      } else if (expression.charAt(right) == EXPRESSION) {
        match++;
      }
      right++;
    }
    put("expression", expression.substring(left, right - 1));
    jdbcTypeOpt(expression, right);
  }

  /**
   * 解析单个属性
   * 例如 "id,"  那么这个key=property value=id
   * @param expression 整个解析表达式
   * @param left 当没有空格的位置
   */
  private void property(String expression, int left) {
    if (left < expression.length()) {
      int right = skipUntil(expression, left, ",:");
      put("property", trimmedStr(expression, left, right));
      jdbcTypeOpt(expression, right);
    }
  }

  /**
   * 跳过空格
   * 例如 "  1233" 返回是1对应索引位置
   * @param expression 表达式
   * @param p 开始位置
   * @return 返回没有空格字符的索引的位置
   */
  private int skipWS(String expression, int p) {
    for (int i = p; i < expression.length(); i++) {
      // 0x20表示空格，也就是ACSII打印字符的一个字符
      if (expression.charAt(i) > 0x20) {
        return i;
      }
    }
    return expression.length();
  }

  /**
   * 判断包含endChars的索引的位置，否则返回整个表达式字符串
   * @param expression  表达式
   * @param p  开始位置
   * @param endChars  结束字符
   * @return
   */
  private int skipUntil(String expression, int p, final String endChars) {
    for (int i = p; i < expression.length(); i++) {
      char c = expression.charAt(i);
      if (endChars.indexOf(c) > -1) {
        return i;
      }
    }
    return expression.length();
  }

  /**
   * 解析java类型和可选项的值
   * @param expression
   * @param p
   */
  private void jdbcTypeOpt(String expression, int p) {
    p = skipWS(expression, p);
    if (p < expression.length()) {
      if (expression.charAt(p) == Character.valueOf(JDBCTYPE)) {
        jdbcType(expression, p + 1);
      } else if (expression.charAt(p) == ATTRIBUTE) {
        option(expression, p + 1);
      } else {
        throw new BuilderException("Parsing error in {" + expression + "} in position " + p);
      }
    }
  }

  /**
   * jdbcType类型
   * @param expression
   * @param p
   */
  private void jdbcType(String expression, int p) {
    int left = skipWS(expression, p);
    int right = skipUntil(expression, left, String.valueOf(ATTRIBUTE));
    if (right > left) {
      put("jdbcType", trimmedStr(expression, left, right));
    } else {
      throw new BuilderException("Parsing error in {" + expression + "} in position " + p);
    }
    option(expression, right + 1);
  }

  /**
   * 可选择项解析
   * @param expression
   * @param p
   */
  private void option(String expression, int p) {
    int left = skipWS(expression, p);
    if (left < expression.length()) {
      int right = skipUntil(expression, left, "=");
      String name = trimmedStr(expression, left, right);
      left = right + 1;
      right = skipUntil(expression, left, String.valueOf(ATTRIBUTE));
      String value = trimmedStr(expression, left, right);
      put(name, value);
      option(expression, right + 1);
    }
  }

  /**
   * 只去掉前后空格 (字符串中间的不去掉)
   * @param str 字符串
   * @param start 开始位置
   * @param end 结束位置
   * @return 返回去掉前后空格的结果
   */
  private String trimmedStr(String str, int start, int end) {
    while (str.charAt(start) <= 0x20) {
      start++;
    }
    while (str.charAt(end - 1) <= 0x20) {
      end--;
    }
    return start >= end ? "" : str.substring(start, end);
  }

}
