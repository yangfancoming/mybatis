package org.apache.goat.chapter100.E.E018;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee2;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E018/mybatis-config.xml";

  /**
   * 使用 association 定义关联的单个对象的封装规则
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee = mapper.getEmpAndDept(1);
    System.out.println(employee);
  }



}
