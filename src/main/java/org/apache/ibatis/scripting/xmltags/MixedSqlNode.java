
package org.apache.ibatis.scripting.xmltags;

import java.util.List;

/**
 识别组合模式的一个要点：实现了一个接口，又聚合了这个接口的集合，那么该类很有可能是组合模式中的组合对象；
*/
public class MixedSqlNode implements SqlNode {

  private final List<SqlNode> contents;

  public MixedSqlNode(List<SqlNode> contents) {
    this.contents = contents;
  }

  @Override
  public boolean apply(DynamicContext context) {
    contents.forEach(node -> node.apply(context));
    return true;
  }
}
