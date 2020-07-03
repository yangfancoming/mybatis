package org.apache.goat.chapter100.C.C001;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C001/mybatis-config.xml";

  EmployeeMapper mapper;

  /**
   * mybatis 允许 CRUD 的返回值  直接定义以下返回值类型
   * Integer  Long  boolean  mybatis  void 会自动给我们返回对应的值
   */
  @BeforeEach
  public void test() throws Exception {
    setUpByReader(XMLPATH);
    mapper = sqlSession.getMapper(EmployeeMapper.class);
  }

  // 查询
  @Test
  void getEmpById()   {
    Employee employee = mapper.getEmpById(1);
    System.out.println(employee);
  }

  // 插入
  @Test
  void addEmp()   {
    Employee employee = new Employee("goat","642744551@qq.com","1");
    Long result = mapper.addEmp(employee);
    System.out.println(result);
  }

  // 修改
  @Test
  void updateEmp()   {
    Employee employee = new Employee(9,"111","1111@qq.com","9");
    boolean b = mapper.updateEmp(employee);
    System.out.println(b);
  }

  // 删除
  @Test
  void deleteEmpById()   {
    Integer integer = mapper.deleteEmpById(8);
    System.out.println(integer);
  }
}
