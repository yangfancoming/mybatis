package org.apache.goat.chapter100.E.E060;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;


public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E051/mybatis-config.xml";

  /**
   * [2019-10-24 19:53:49,169]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==>  Preparing: select * from tbl_employee where 1=1 and id = ?
   * [2019-10-24 19:53:49,206]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==> Parameters: 1(Integer)
   * [2019-10-24 19:53:49,223]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==    Columns: id, last_name, gender, email, d_id
   * [2019-10-24 19:53:49,224]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==        Row: 1, tom, 0, tom@qq.com, null
   * [2019-10-24 19:53:49,227]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:<==      Total: 1
  */
  @Test
  void Reader1() throws Exception {
    setUpByReader(XMLPATH);
    List<Employee> objects2 = sqlSession.selectList("com.goat.test.namespace.testIf", 1);
    System.out.println(objects2);
  }

  @Test
  void Reader2() throws Exception {
    setUpByReader(XMLPATH);
    List<Employee> objects1 = sqlSession.selectList("com.goat.test.namespace.testIf");
    System.out.println(objects1);
  }
}
