
package org.apache.ibatis.mapping;

/**
 * Represents the content of a mapped statement read from an XML file or an annotation.
 * 表示从XML文件或注解读取的映射语句的内容。
 * It creates the SQL that will be passed to the database out of the input parameter received from the user.
 * 它创建将从用户接收的输入参数中传递给数据库的sql
 */
public interface SqlSource {

//  通过解析得到 BoundSql 对象 BoundSql 对象会在后面具体介绍，其中封装了包含 ” ？”占位符的 SQL语句，以及绑定的 实参
  BoundSql getBoundSql(Object parameterObject);

}
