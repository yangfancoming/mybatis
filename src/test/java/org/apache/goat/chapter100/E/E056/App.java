package org.apache.goat.chapter100.E.E056;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E056/mybatis-config.xml";

  /**
   *
   */
  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH);
    CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
    Customer customer = new Customer();
    customer.setId(2);
    List<Customer> list = mapper.getTest(customer);
    System.out.println(list);
  }


  @Test
  void test2() throws Exception  {
    setUpByReader(XMLPATH);
    CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
    Customer customer = new Customer();
    customer.setId(2);
    List<Customer> list = mapper.getTest2(customer);
    System.out.println(list);
  }

  @Test
  void test3() throws Exception  {
    setUpByReader(XMLPATH);
    CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
    Customer test3 = mapper.getTest3(1);
    System.out.println(test3);
  }


}
