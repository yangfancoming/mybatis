
package org.apache.ibatis.mapping;

/**
 * Represents the content of a mapped statement read from an XML file or an annotation.
 * It creates the SQL that will be passed to the database out of the input parameter received from the user.
 */
public interface SqlSource {

//  通过解析得到 BoundSql 对象 BoundSql 对象会在后面具体介绍，其中封装了包含 ” ？”占位符的 SQL语句，以及绑定的 实参
  BoundSql getBoundSql(Object parameterObject);

}
