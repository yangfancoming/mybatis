package org.apache.goat.chapter100.C.C004;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C004/mybatis-config.xml";

  /**
   * 插入 完成后  可以看到结果
   * Employee{id=14, lastName='goat', email='642744551@qq.com', gender='1'}
   * mybatis 将插入的自增主键 赋值给 Employee 实体类的 id 属性
   */
  @Test
  void addEmp() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = new Employee("goat","642744551@qq.com","1");
    mapper.addEmp(employee);
    System.out.println(employee);
  }

  @Test
  void addEmp2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = new Employee("goat","642744551@qq.com","1");
    mapper.addEmp2(employee);
    System.out.println(employee);
  }

}
