package org.apache.goat.chapter100.E.E001;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E001/mybatis-config.xml";

  /**
   * resultType：如果返回的是一个集合，要写集合中元素的类型
   * java 中 返回值是  List<Employee>
   *  对应 局部xnl中是 <select id="getEmpsByLastNameLike" resultType="org.apache.goat.common.Employee">
  */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    List<Employee> like = mapper.getEmpsByLastNameLike("%g%"); // 模糊查询 字段中 带有 g 字符的
    System.out.println(like);
  }


}
