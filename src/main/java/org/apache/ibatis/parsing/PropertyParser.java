
package org.apache.ibatis.parsing;

import java.util.Properties;

/**
 属性解析器，主要用于对默认值的解析
*/
public class PropertyParser {

  private static final String KEY_PREFIX = "org.apache.ibatis.parsing.PropertyParser.";
  /**
   * The special property key that indicate whether enable a default value on placeholder.
   *   The default value is {@code false} (indicate disable a default value on placeholder)
   *   If you specify the {@code true}, you can specify key and default value on placeholder (e.g. {@code ${db.username:postgres}}).
   * @since 3.4.2
   *
   * 特殊属性键，指示是否在占位符上启用默认值。
   * 默认值是false，是禁用的占位符上使用默认值，当启用以后（true）可以在占位符上使用默认值。
   * 例如：${db.username:postgres}，表示数据库的用户名默认是postgres
   */
  public static final String KEY_ENABLE_DEFAULT_VALUE = KEY_PREFIX + "enable-default-value";

  /** 为占位符上的键和默认值指定分隔符的特殊属性键。 默认分隔符是“:”
   * The special property key that specify a separator for key and default value on placeholder.
   *   The default separator is {@code ":"}.
   * @since 3.4.2
   */
  public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";

  private static final String ENABLE_DEFAULT_VALUE = "false";
  private static final String DEFAULT_VALUE_SEPARATOR = ":";

  private PropertyParser() {
    // Prevent Instantiation   // 私有构造函数，防止实例化
  }

  public static String parse(String string, Properties variables) {
    //解析默认值
    VariableTokenHandler handler = new VariableTokenHandler(variables);
    //解析占位符
    GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
    return parser.parse(string);
  }

  /**
   * 内部私有静态类
   */
  private static class VariableTokenHandler implements TokenHandler {
    // <properties> 节点下定义的键值对，用于替换占位符
    private final Properties variables;
    // 是否启用默认值
    private final boolean enableDefaultValue;
    // 默认分隔符
    private final String defaultValueSeparator;

    private VariableTokenHandler(Properties variables) {
      this.variables = variables;
      this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFAULT_VALUE, ENABLE_DEFAULT_VALUE));
      this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
    }

    private String getPropertyValue(String key, String defaultValue) {
      return (variables == null) ? defaultValue : variables.getProperty(key, defaultValue);
    }

    @Override
    public String handleToken(String content) {
      if (variables != null) {
        String key = content;
        if (enableDefaultValue) {
          //分隔符索引值
          final int separatorIndex = content.indexOf(defaultValueSeparator);
          String defaultValue = null;
          if (separatorIndex >= 0) {
            key = content.substring(0, separatorIndex);
            //获取默认值
            defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());
          }
          if (defaultValue != null) {
            //优先使用变量集合中的值，其次使用默认值
            return variables.getProperty(key, defaultValue);
          }
        }
        if (variables.containsKey(key)) {
          return variables.getProperty(key);
        }
      }
      return "${" + content + "}";
    }
  }

}
