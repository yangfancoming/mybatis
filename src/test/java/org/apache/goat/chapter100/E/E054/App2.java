package org.apache.goat.chapter100.E.E054;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee2;
import org.junit.jupiter.api.Test;

import java.util.List;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E054/mybatis-config.xml";

  /**
   *
   *    由于 if 查询的时候如果某些条件没带可能sql拼装会有问题  解决方法由两种：
   *    1、给where后面加上1=1，以后的条件都and xxx.
   *    2、mybatis使用where标签来将所有的查询条件包括在内。mybatis就会将where标签中拼装的sql，多出来的and或者or去掉
   *    where只会去掉第一个多出来的and或者or
   *
   *    但是由于 <where></where> 标签中的 and 只能写在语句前面 不能放在后面 所以 还是推荐使用第一种解决方式！！！
   */

  /**
   * [2019-10-23 19:24:29,604]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==>  Preparing: select * from tbl_employee WHERE id=?
   * [2019-10-23 19:24:29,629]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==> Parameters: 2(Integer)
   * [2019-10-23 19:24:29,643]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==    Columns: id, last_name, gender, email, d_id
   * [2019-10-23 19:24:29,644]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==        Row: 2, jane, 1, jane@qq.com, null
   * [2019-10-23 19:24:29,646]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:<==      Total: 1
   * [Employee{id=2, lastName='null', email='jane@qq.com', gender='1'}]
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
   * [2019-10-23 19:25:45,443]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==>  Preparing: select * from tbl_employee WHERE id=? and last_name like ?
   * [2019-10-23 19:25:45,473]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==> Parameters: 2(Integer), jane(String)
   * [2019-10-23 19:25:45,490]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==    Columns: id, last_name, gender, email, d_id
   * [2019-10-23 19:25:45,490]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==        Row: 2, jane, 1, jane@qq.com, null
   * [2019-10-23 19:25:45,494]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:<==      Total: 1
   * [Employee{id=2, lastName='null', email='jane@qq.com', gender='1'}]
  */
  @Test
  void test2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee2 = new Employee2();
    employee2.setId(2);
    employee2.setLastName("jane");
    employee2.setEmail("jane@qq.com");
    employee2.setGender("1");
    List<Employee2> list = mapper.getEmpsByConditionIf(employee2);
    System.out.println(list);
  }

}
