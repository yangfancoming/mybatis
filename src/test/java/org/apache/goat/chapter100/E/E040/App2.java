package org.apache.goat.chapter100.E.E040;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.model.Employee2;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E040/mybatis-config.xml";

  /**
   * case 0  会把 部门查询出来
   */
  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee = mapper.getEmpById(1);
    System.out.println(employee);
  }


  /**
   * case 1 不会去查询部门   而是 用lastName 更改 email
   */
  @Test
  void test2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee = mapper.getEmpById(2);
    System.out.println(employee);
  }
}
