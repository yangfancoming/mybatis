
package org.apache.goat.chapter500.PropertyParser;



import org.apache.goat.chapter500.common.GenericTokenParser;
import org.apache.goat.chapter500.common.TokenHandler;

import java.util.Properties;

/**
 属性解析器，主要用于对默认值的解析
*/
public class PropertyParser {

  private static final String KEY_PREFIX = "org.apache.ibatis.parsing.PropertyParser.";
  /**
   * The special property key that indicate whether enable a default value on placeholder.
   *   The default value is false (indicate disable a default value on placeholder)
   *   If you specify the true, you can specify key and default value on placeholder (e.g. {@code ${db.username:postgres}}).
   * @since 3.4.2
   *
   * 特殊属性键，指示是否在占位符上启用默认值。
   * 默认值是false，当启用以后（true）可以在占位符上使用默认值。
   * 例如：${db.username:postgres}，表示数据库的用户名默认是postgres
   */
  public static final String KEY_ENABLE_DEFAULT_VALUE = KEY_PREFIX + "enable-default-value";

  /**
   * The special property key that specify a separator for key and default value on placeholder. The default separator is { ":" }.
   * @since 3.4.2
   */
  public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";

  private static final String ENABLE_DEFAULT_VALUE = "false";
  private static final String DEFAULT_VALUE_SEPARATOR = ":";

  private PropertyParser() {
    // Prevent Instantiation   // 私有构造函数，防止实例化
  }

  /**
   * @Description: 根据指定的占位符数据字典 解析传入的字符串
   * @date 2019年11月13日20:13:32
   * @param string 待解析的字符串  eg: "${key1}"
   * @param variables Properties 占位符替换字典 里面包含了 要替换成的值 eg:  props.setProperty("key1", "goat");
   * @return 解析完成后的字符串结果
   */
  public static String parse(String string, Properties variables) {
    //解析默认值
    VariableTokenHandler handler = new VariableTokenHandler(variables);
    //解析占位符
    //在初始化GenericTokenParser对象，设置openToken为${,endToken为}
    //有没有对${}比较熟悉，这个符号就是mybatis配置文件中的占位符，例如定义datasource时用到的 <property name="driverClassName" value="${driver}" />
    //同时也可以解释在VariableTokenHandler中的handleToken时，如果content在properties中不存在时，返回的内容要加上${}了。
    GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
    return parser.parse(string);
  }

  /**
   * 内部私有静态类
   * VariableTokenHandler实现了TokenHandler接口，包含了一个Properties类型的属性，在初始化这个类时需指定该属性的值
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
      //如果variables不为空且存在key为content的property，就从variables中返回具体的值，否则在content两端添加上${和}
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
