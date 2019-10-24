
package org.apache.ibatis.scripting.xmltags;


public class VarDeclSqlNode implements SqlNode {

  //<bind>节点名称
  private final String name;
  //<bind>节点的value表达式
  private final String expression;

  public VarDeclSqlNode(String var, String exp) {
    name = var;
    expression = exp;
  }

  @Override
  public boolean apply(DynamicContext context) {
    //解析OGNL表达式的值
    final Object value = OgnlCache.getValue(expression, context.getBindings());
    //将name,value存入DynamicContext的bindings集合中，提供其他SqlNode的下一步解析
    context.bind(name, value);
    return true;
  }

}
