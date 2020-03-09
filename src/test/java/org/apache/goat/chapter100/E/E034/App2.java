package org.apache.goat.chapter100.E.E034;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.model.Department;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E034/mybatis-config.xml";

  /**
   * collection 分步查询&延迟加载
   */
  @Test
  void getEmpByIdStep() throws Exception  {
    setUpByReader(XMLPATH);
    DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
    Department employee = mapper.getDeptByIdStep(1);
//    System.out.println(employee); // 急切加载
    System.out.println(employee.getDepartmentName()); // 懒加载
  }



}
