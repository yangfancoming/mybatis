
package org.apache.ibatis.scripting.xmltags;


/**
 * Sql 源接口，代表从xml文件或注解映射的sql内容，主要就是用于创建BoundSql，有实现类DynamicSqlSource(动态Sql源)，StaticSqlSource(静态Sql源)等：
 * 代表着组合模式中的容器
*/
public interface SqlNode {
  /**
    apply() 是 SqlNode 接口中定义的唯一方法，该方法会根据用户传入的实参，参数解析该 SqlNode 所记录的动态SQL节点，
    并调用 DynamicContext.appendSql()方法将解析后的 SQL 片段追加到 DynamicContext.sqlBuilder 中保存
    当SQL节点下的所有 SqlNode 完成解析后，我们就可以从 DynamicContext 中获取一条动态生成的完整的 SQL 语句

   apply方法是所有的动态节点都实现的接口

   将各Sql片段合并到DynamicContext中，拼接称为完整的SQL
  */
  boolean apply(DynamicContext context);
}
