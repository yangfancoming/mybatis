package org.apache.goat.chapter600.item01;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter600/item01/mybatis-config.xml";

  /**
   * [2019-10-23 19:34:46,180]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==>  Preparing: SELECT id, name, email FROM customers WHERE name = ?
   * [2019-10-23 19:34:46,231]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:==> Parameters: 汤唯(String)
   * [2019-10-23 19:34:46,247]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==    Columns: id, name, email
   * [2019-10-23 19:34:46,247]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.trace(BaseJdbcLogger.java:141)TRACE:<==        Row: 4, 汤唯, tangw@sina.com
   * [2019-10-23 19:34:46,251]org.apache.ibatis.logging.jdbc.BaseJdbcLogger.debug(BaseJdbcLogger.java:135)DEBUG:<==      Total: 1
   */
  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH);
    CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
    Customer customer = new Customer();
    customer.setName("汤唯");
    List<Customer> list = mapper.testSelect(customer);
    System.out.println(list);
  }
}
