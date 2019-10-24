
package org.apache.ibatis.scripting.xmltags;


/**
 * StaticTextSqlNode很简单，就是直接返回SQL语句
*/
public class StaticTextSqlNode implements SqlNode {

  private final String text;

  public StaticTextSqlNode(String text) {
    this.text = text;
  }

  @Override
  public boolean apply(DynamicContext context) {
    context.appendSql(text);
    return true;
  }

}
