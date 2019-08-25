package org.apache.goat.chapter100.E050;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee2;
import org.junit.jupiter.api.Test;

import java.util.List;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E050/mybatis-config.xml";

  /**
   * 传递 id 参数的sql
   * select * from tbl_employee where 1=1 and id = ?
   */
  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee2 = new Employee2();
    employee2.setId(2);
    List<Employee2> list = mapper.getEmpsByConditionIf(employee2);
    System.out.println(list);
  }


  /**
   * 未传递 id 参数的sql
   * Preparing: select * from tbl_employee where 1=1
   */
  @Test
  void test2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee2 = new Employee2();
    List<Employee2> list = mapper.getEmpsByConditionIf(employee2);
    System.out.println(list);
  }



  /**
   *
   *  642744551@qq.com(String), 1(String)
   */
  @Test
  void test3() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee2 = new Employee2();
    employee2.setEmail("642744551@qq.com");// 测试 trim()
    employee2.setGender("1");// 测试 trim()
    List<Employee2> list = mapper.getEmpsByConditionIf(employee2);
    System.out.println(list);
  }
}
