
package org.apache.ibatis.executor.resultset;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;

/**
 ResultSetHandler负责处理两件事：
 （1）处理Statement执行后产生的结果集，生成结果列表
 （2）处理存储过程执行后的输出参数
 ResultSetHandler是一个接口，提供了两个函数分别用来处理普通操作和存储过程的结果：

 ResultSetHandler的具体实现类是DefaultResultSetHandler，其实现的步骤就是将Statement执行后的结果集，
 按照Mapper文件中配置的ResultType或ResultMap来封装成对应的对象，最后将封装的对象返回。
*/
public interface ResultSetHandler {

  //对普通查询到的结果转换
  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

  //调用存储过程返回结果，将结果值放在参数中
  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
