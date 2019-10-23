package org.apache.goat.chapter600.item01;


import org.apache.goat.common.Customer;

import java.util.List;

public interface CustomerMapper {

  //携带了哪个字段查询条件就带上这个字段的值
  public List<Customer> getTest(Customer employee);

  public List<Customer> getTest2(Customer employee);
}
