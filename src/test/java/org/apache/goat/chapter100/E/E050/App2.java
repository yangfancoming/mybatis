package org.apache.goat.chapter100.E.E050;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee2;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 源码位置： <if> 标签解析
 * @see XMLScriptBuilder.NodeHandler#handleNode(org.apache.ibatis.parsing.XNode, java.util.List)
 */
class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E050/mybatis-config.xml";

  /**
   * [2019-10-24 19:49:02,653]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==>  Preparing: select * from tbl_employee where 1=1 and id = ?
   * [2019-10-24 19:49:02,727]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==> Parameters: 1(Integer)
   * [2019-10-24 19:49:02,773]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==    Columns: id, last_name, gender, email, d_id
   * [2019-10-24 19:49:02,773]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==        Row: 1, tom, 0, tom@qq.com, null
   * [2019-10-24 19:49:02,791]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:<==      Total: 1
   */
  @Test
  void test4() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee2 = new Employee2();
    employee2.setId(1);
    List<Employee2> list = mapper.testIf(employee2);
    Assert.assertEquals(1,list.size());
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
    Assert.assertEquals(9,list.size());
  }

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
    Assert.assertEquals(1,list.size());
  }

  /**
   *  select * from tbl_employee where 1=1 and email=? and gender=?
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
    Assert.assertEquals(7,list.size());
  }
}
