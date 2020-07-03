package org.apache.goat.chapter100.C.C010;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C/C010/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";

  /**
   *    单个参数： mybatis 不会做处理  所以 #{ xxxx } 中的参数名xxxx 可以随意写
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpById(2);
    System.out.println(employee);
  }
}
