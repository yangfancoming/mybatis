package org.apache.goat.chapter100.E054;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee2;
import org.junit.jupiter.api.Test;

import java.util.List;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E054/mybatis-config.xml";

  /**
   *
   *    由于 if 查询的时候如果某些条件没带可能sql拼装会有问题  解决方法由两种：
   *    1、给where后面加上1=1，以后的条件都and xxx.
   *    2、mybatis使用where标签来将所有的查询条件包括在内。mybatis就会将where标签中拼装的sql，多出来的and或者or去掉
   *    where只会去掉第一个多出来的and或者or
   *
   *    但是由于 <where></where> 标签中的 and 只能写在语句前面 不能放在后面 所以 还是推荐使用第一种解决方式！！！
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


}
