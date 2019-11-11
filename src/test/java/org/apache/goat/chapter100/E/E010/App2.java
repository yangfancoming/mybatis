package org.apache.goat.chapter100.E.E010;


import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E010/mybatis-config.xml";

  /**
   * 正常查询 可以看到 lastName='null'  因为 与 数据库表名 last_name 不对应
   * Employee{id=1, lastName='null', email='tom@qq.comMysql', gender='0'}
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpById2(1);
    System.out.println(employee);
  }

  /**
   *  加入 <resultMap> 标签后的查询  就可以查询出结果了
   * Employee{id=1, lastName='tom', email='tom@qq.comMysql', gender='0'}
  */
  @Test
  void test() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee temp = mapper.getEmpById(1);
    System.out.println(temp);
  }

}
