package org.apache.goat.chapter100.E.E056;


import org.apache.goat.common.model.Customer;

import java.util.List;

 interface CustomerMapper {

  //携带了哪个字段查询条件就带上这个字段的值
   List<Customer> getTest(Customer employee);

   List<Customer> getTest2(Customer employee);

   Customer getTest3(Integer id);

   int getTest4();

   Customer selectById(Integer id);
}
