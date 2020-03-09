package org.apache.goat.chapter100.C.C010;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C010/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";

  /**
   *     多个参数： mybatis 会做特殊处理   多个参数会被封装成一个map
   *                  key： param1.....paramN
   *                 value： 传入的参数
   *                #{ } 则是从map中获取指定key的值
   *
   * Parameter 'id' not found. Available parameters are [arg1, arg0, param1, param2]
   */
  @Test
  void getEmpByIdAndLastName() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpByIdAndLastName(15,"goat");
    System.out.println(employee);
  }

  /**
   * 多个参数 使用   select * from tbl_employee where id = #{param1} and last_name = #{param2}
   */
  @Test
  void getEmpByIdAndLastName1() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpByIdAndLastName1(15,"goat");
    System.out.println(employee);
  }

  /**
   * 多个参数 使用   select * from tbl_employee where id = #{arg0} and last_name = #{arg1}
  */
  @Test
  void getEmpByIdAndLastName2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpByIdAndLastName2(10,"goat");
    System.out.println(employee);
  }
}
