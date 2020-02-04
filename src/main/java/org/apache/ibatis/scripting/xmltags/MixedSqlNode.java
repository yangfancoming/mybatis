
package org.apache.ibatis.scripting.xmltags;

import java.util.List;

/**
 识别组合模式的一个要点：实现了一个接口，又聚合了这个接口的集合，那么该类很有可能是组合模式中的组合对象；
 其中MixedSqlNode是 容器节点，TextSqlNode 是 叶节点..
*/
public class MixedSqlNode implements SqlNode {
  //记录sql节点中的所有SQL片段
  private final List<SqlNode> contents;

  public MixedSqlNode(List<SqlNode> contents) {
    this.contents = contents;
  }

  @Override
  public boolean apply(DynamicContext context) {
    //NOTE: 循环调用contents集合中的所有SqlNode对象的apply方法
    contents.forEach(node -> node.apply(context));
    return true;
  }

}
