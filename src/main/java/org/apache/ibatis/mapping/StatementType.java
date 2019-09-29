
package org.apache.ibatis.mapping;

/**
 * 取值说明： 
 *   1、STATEMENT:直接操作sql，不进行预编译，获取数据：$—Statement 
 *   2、PREPARED:预处理，参数，进行预编译，获取数据：#—–PreparedStatement:默认 
 *   3、CALLABLE:执行存储过程————CallableStatement 
 *   另外说明下：
 *   如果只为STATEMENT，那么sql就是直接进行的字符串拼接，这样为字符串需要加上引号，
 *   如果为PREPARED，是使用的参数替换，也就是索引占位符，我们的#会转换为?再设置对应的参数的值
*/

public enum StatementType {
  STATEMENT, PREPARED, CALLABLE
}
