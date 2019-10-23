
package org.apache.ibatis.scripting.xmltags;

import java.util.regex.Pattern;

import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.scripting.ScriptingException;
import org.apache.ibatis.type.SimpleTypeRegistry;

/**
 扮演了树叶节点的角色
*/
public class TextSqlNode implements SqlNode {

  private final String text;

  private final Pattern injectionFilter;

  public TextSqlNode(String text) {
    this(text, null);
  }

  public TextSqlNode(String text, Pattern injectionFilter) {
    this.text = text;
    this.injectionFilter = injectionFilter;
  }

  public boolean isDynamic() {
    DynamicCheckerTokenParser checker = new DynamicCheckerTokenParser();
    GenericTokenParser parser = createParser(checker);
    parser.parse(text);
    return checker.isDynamic();
  }

  @Override
  public boolean apply(DynamicContext context) {
    //将动态SQL（带${}占位符的SQL）解析成完成SQL语句的解析器，即将${}占位符替换成实际的变量值
    GenericTokenParser parser = createParser(new BindingTokenParser(context, injectionFilter));
    //将解析后的SQL片段添加到DynamicContext中
    context.appendSql(parser.parse(text));
    return true;
  }

  private GenericTokenParser createParser(TokenHandler handler) {
    return new GenericTokenParser("${", "}", handler);
  }

  /**
   * 内部类，继承了TokenHandler接口，它的主要作用是根据DynamicContext.bindings集合中的信息解析SQL语句节点中的${}占位符
  */
  private static class BindingTokenParser implements TokenHandler {

    private DynamicContext context;
    //需要匹配的正则表达式
    private Pattern injectionFilter;

    public BindingTokenParser(DynamicContext context, Pattern injectionFilter) {
      this.context = context;
      this.injectionFilter = injectionFilter;
    }

    @Override
    public String handleToken(String content) {
      //获取用户提供的实参
      Object parameter = context.getBindings().get("_parameter");
      //如果实参为null
      if (parameter == null) {
        //将参考上下文的value key设为null
        context.getBindings().put("value", null);
        //如果实参是一个常用数据类型的类（Integer.class,String.class,Byte.class等等）
      } else if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
        //将参考上下文的value key设为该实参
        context.getBindings().put("value", parameter);
      }
      //通过OGNL解析参考上下文的值
      Object value = OgnlCache.getValue(content, context.getBindings());
      String srtValue = value == null ? "" : String.valueOf(value); // issue #274 return "" instead of "null"
      //检测合法性
      checkInjection(srtValue);
      return srtValue;
    }

    private void checkInjection(String value) {
      if (injectionFilter != null && !injectionFilter.matcher(value).matches()) {
        throw new ScriptingException("Invalid input. Please conform to regex" + injectionFilter.pattern());
      }
    }
  }

  private static class DynamicCheckerTokenParser implements TokenHandler {

    private boolean isDynamic;

    public DynamicCheckerTokenParser() {
      // Prevent Synthetic Access
    }

    public boolean isDynamic() {
      return isDynamic;
    }

    @Override
    public String handleToken(String content) {
      this.isDynamic = true;
      return null;
    }
  }

}
