package org.apache.goat.chapter100.C.C010;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


class App4 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C010/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";

  /**
   * POJO 传参
   * 由于参数命名的都是实体类中的属性，如果有多个参数 还是写起来很麻烦
   * 由此，pojo传参 应运而生
   *      如果参数很多 可以传入 pojo
   *             #{ 属性名 } 即可取出传入的pojo的属性值
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = new Employee();
    employee.setId(10);
    employee.setLastName("goat");
    Employee temp = mapper.getEmpByIdAndLastName4(employee);
    System.out.println(temp);
  }

}
