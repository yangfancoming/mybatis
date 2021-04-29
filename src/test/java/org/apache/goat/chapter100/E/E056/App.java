package org.apache.goat.chapter100.E.E056;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Customer;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E056/mybatis-config.xml";

  CustomerMapper mapper;

  @BeforeEach
  public void test() throws Exception {
    setUpByReader(XMLPATH);
    mapper = sqlSession.getMapper(CustomerMapper.class);
  }

  @Test
  void test1() {
    List<Customer> list = mapper.getTest( new Customer(2));
    System.out.println(list);
  }

  @Test
  void test2() {
    List<Customer> list = mapper.getTest2(new Customer(2));
    System.out.println(list);
  }

  @Test
  void test3() {
    Customer test3 = mapper.getTest3(1);
    System.out.println(test3);
  }

  @Test
  void temp()  {
    Configuration configuration = sqlSessionFactory.getConfiguration();
    Map<String, XNode> sqlFragments = configuration.getSqlFragments();
    System.out.println(sqlFragments);
  }
}
