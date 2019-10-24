
package org.apache.ibatis.scripting.xmltags;

/**
 org.apache.ibatis.scripting.xmltags.ExpressionEvaluator
 主要是应用OGNL语法进行解析类似name !=null，其会读取上下文中是否有对应的属性值。具体的读者可自行分析
 IfSqlNode是解析<if>节点
 */
public class IfSqlNode implements SqlNode {

  //用于解析<if>节点的test表达式的值
  private final ExpressionEvaluator evaluator;
  //记录<if>节点中test表达式
  private final String test;
  //记录了<if>节点的子节点
  private final SqlNode contents;

  public IfSqlNode(SqlNode contents, String test) {
    this.test = test;
    this.contents = contents;
    this.evaluator = new ExpressionEvaluator();
  }

  /**
   * 组合模式： 这里会链式调用
   * IfSqlNode  ---> MixedSqlNode  --->  StaticTextSqlNode --->  TrimSqlNode --->  DynamicContext
  */
  @Override
  public boolean apply(DynamicContext context) {
    //主要作用即是用于条件的判断  //检测test属性中记录的表达式
    if (evaluator.evaluateBoolean(test, context.getBindings())) {
      //如果test表达式为true,则执行子节点的apply()方法
      contents.apply(context);
      return true;//返回test表达式的结果为true
    }
    return false;//返回test表达式的结果为false
  }

}
