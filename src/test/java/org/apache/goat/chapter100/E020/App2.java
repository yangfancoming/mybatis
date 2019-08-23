package org.apache.goat.chapter100.E020;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee2;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E020/mybatis-config.xml";

  /**
   * association 分步查询 可以看到运行结果  执行了2次 sql
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee = mapper.getEmpByIdStep(1);
    System.out.println(employee);
  }



}
