package org.apache.goat.chapter100.C.C001;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C001/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";

  /**
   * 查询
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpById(1);
    System.out.println(employee);
  }

  /**
   * 插入
   */
  @Test
  void addEmp() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = new Employee("goat","642744551@qq.com","1");
    Long result = mapper.addEmp(employee);
    System.out.println(result);
  }

  /**
   * 修改
   */
  @Test
  void updateEmp() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = new Employee(9,"111","1111@qq.com","9");
    boolean b = mapper.updateEmp(employee);
    System.out.println(b);
  }

  /**
   * 删除
   */
  @Test
  void deleteEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    mapper.deleteEmpById(8);
  }
}
