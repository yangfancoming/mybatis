
package org.apache.ibatis.scripting.xmltags;

/**
 org.apache.ibatis.scripting.xmltags.ExpressionEvaluator主要是应用OGNL语法进行解析类似name !=null，其会读取上下文中是否有对应的属性值。具体的读者可自行分析
 */
public class IfSqlNode implements SqlNode {
  private final ExpressionEvaluator evaluator;
  private final String test;
  private final SqlNode contents;

  public IfSqlNode(SqlNode contents, String test) {
    this.test = test;
    this.contents = contents;
    this.evaluator = new ExpressionEvaluator();
  }

  @Override
  public boolean apply(DynamicContext context) {
    //主要作用即是用于条件的判断
    if (evaluator.evaluateBoolean(test, context.getBindings())) {
      contents.apply(context);
      return true;
    }
    return false;
  }

}
