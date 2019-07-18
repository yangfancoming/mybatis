
package org.apache.ibatis.scripting.xmltags;


public interface SqlNode {
  /**
    apply （）是 SqlNode 接 口中定义的唯一方法，该方法会根据用户传入的实参，参数解析该 SqlNode 所
   记录的动态SQL节点，并调用 DynamicContext.appendSql()方法将解析后的 SQL 片段追加到
   DynamicContext.sqlBuilder 中保存
   当SQL节点下的所有 SqlNode 完成解析后，我们就可以从 DynamicContext 中获取一条动态生成的完整的 SQL 语句
  */
  boolean apply(DynamicContext context);
}
