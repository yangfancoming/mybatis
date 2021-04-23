
package org.apache.ibatis.mapping;

/**
 * Represents the content of a mapped statement read from an XML file or an annotation.
 * 表示从XML文件或注解读取的映射语句的内容。
 * It creates the SQL that will be passed to the database out of the input parameter received from the user.
 * 它创建将从用户接收的输入参数中传递给数据库的sql
 *
 * SqlNode实现	   对应SQL语句中的类型
 * TextSqlNode	  ${}
 * IfSqlNode	   If节点
 * TrimSqlNode/WhereSqlNode/SetSqlNode	  Trim/Where/Set节点
 * Foreach节点   	foreach标签
 * ChooseSqlNode节点	  choose/when/otherwhise节点
 * ValDeclSqlNode节点	   bind节点
 * StaticTextSqlNode	   不含上述节点
 * 除了StaticTextSqlNode节点外，其余对应的都是动态语句。
 *
 * 美井
 * 在编写mybatis的sql语句的时候，经常用到的是#{}的字符去替代其中的查询入参，偶尔也会在网上看到${}这样的字符使用
 * 前者 #{} 调用的为 RawSqlSource 帮助类进行生成具体的sql，
 * 后者 ${} 则是通过 DynamicSqlSource 帮助类来实现的。
 * 即：DynamicSqlSource 解析含有${}的sql语句，而 RawSqlSource 解析含有#{}的sql语句
 *
 */
public interface SqlSource {

  //  通过解析得到 BoundSql 对象 BoundSql 对象会在后面具体介绍，其中封装了包含 "?" 占位符的 SQL语句，以及绑定的 实参
  BoundSql getBoundSql(Object parameterObject);

}
