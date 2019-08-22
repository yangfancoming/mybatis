package org.apache.goat.chapter100.C012;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


/**
 * 	区别：
 * 		#{}:是以预编译的形式，将参数设置到sql语句中；PreparedStatement；防止sql注入
 * 		${}:取出的值直接拼装在sql语句中；会有安全问题；
 * 		大多情况下，我们去参数的值都应该去使用#{}；
 *
 *   少数情况下使用 ${}  比如：
 * 		原生jdbc不支持占位符的地方我们就可以使用${}进行取值
 * 		比如  分表  按照年份分表拆分  select * from ${year}_salary where xxx;  （因为表名是不支持 #{} 预编译的）
 * 	        排序  select * from tbl_employee order by ${f_name} ${order}      （因为orderby 字段名和关键字 也是不支持 #{} 预编译的）
 *
 *
 * #{}:更丰富的用法：
 * 	规定参数的一些规则：
 * 	javaType、 jdbcType、 mode（存储过程）、 numericScale、
 * 	resultMap、 typeHandler、 jdbcTypeName、 expression（未来准备支持的功能）
 *
 * 	jdbcType通常需要在某种特定的条件下被设置：
 * 		在我们数据为null的时候，有些数据库可能不能识别mybatis对null的默认处理。比如Oracle（报错）
 * 		JdbcType OTHER：无效的类型；因为mybatis对所有的null都映射的是原生Jdbc的OTHER类型，oracle不能正确处理;
 * 		由于全局配置中：jdbcTypeForNull=OTHER；oracle不支持；两种办法
 * 		1、#{email,jdbcType=OTHER};
 * 		2、jdbcTypeForNull=NULL
 * 			<setting name="jdbcTypeForNull" value="NULL"/>
*/
class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C012/mybatis-config.xml";


  /**
   * Preparing: select * from tbl_employee where id = 15 and last_name = ?
   * Parameters: goat(String)
   * Total: 1
   * 可以看到 参数id=15  直接并没有作为参数传递  而是直接打印在了sql上
  */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpByIdAndLastName3(15,"goat");
    System.out.println(employee);
  }


  // 表名不支持预编译！ 报错： You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near ''tbl_employee'
  @Test
  void getEmpById2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Map<String, Object> map = new HashMap<>();
    map.put("id", 2);
    map.put("lastName", "jane");
    map.put("tableName", "tbl_employee");
    Employee temp = mapper.getEmpByIdAndLastName5(map);
    System.out.println(temp);
  }

  // 使用 ${tableName} 可以正常运行
  @Test
  void getEmpByIdAndLastName4() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Map<String, Object> map = new HashMap<>();
    map.put("id", 2);
    map.put("lastName", "jane");
    map.put("tableName", "tbl_employee");
    Employee temp = mapper.getEmpByIdAndLastName4(map);
    System.out.println(temp);
  }
}
