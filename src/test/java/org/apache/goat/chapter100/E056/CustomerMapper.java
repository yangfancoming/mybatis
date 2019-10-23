package org.apache.goat.chapter100.E056;


import org.apache.goat.common.Customer;

import java.util.List;

public interface CustomerMapper {

  //携带了哪个字段查询条件就带上这个字段的值
  public List<Customer> getTest(Customer employee);
}
