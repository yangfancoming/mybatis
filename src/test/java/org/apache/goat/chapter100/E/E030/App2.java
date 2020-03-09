package org.apache.goat.chapter100.E.E030;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.model.Department;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E030/mybatis-config.xml";

  /**

   */
  @Test
  void getEmpByIdStep() throws Exception  {
    setUpByReader(XMLPATH);
    DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
    Department employee = mapper.getDeptByIdPlus(1);
    System.out.println(employee);

  }



}
