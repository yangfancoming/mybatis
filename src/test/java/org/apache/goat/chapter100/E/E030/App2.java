package org.apache.goat.chapter100.E.E030;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Department;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E030/mybatis-config.xml";

  @Test
  void getEmpByIdStep() throws Exception  {
    setUpByReader(XMLPATH);
    DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
    Department employee = mapper.getDeptByIdPlus(1);
    System.out.println(employee);

  }

  @Test
  public void test(){
    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
  }
}
