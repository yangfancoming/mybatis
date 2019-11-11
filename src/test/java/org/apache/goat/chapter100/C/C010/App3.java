package org.apache.goat.chapter100.C.C010;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


class App3 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C010/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";

  /**
   * 命名参数
   * 由于 使用 [arg1, arg0, param1, param2] 这种方式 在很多参数的情况下  参数名会觉得非常蛋疼
   * 因此 命名参数方式 应运而生
   *      命名参数：@param("明确指定参数名")
   *              key： @param 注解中指定的值
   *              value： 传入的参数
   *              #{ } 则是从map中获取指定key的值
   *
   * 其原理应该是： mybatis 把mapper接口中的获取的参数 put 到map中
   * 然后在局部xml中解析参数时 再从map中 get出来
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpByIdAndLastName3(15,"goat");
    System.out.println(employee);
  }

}
