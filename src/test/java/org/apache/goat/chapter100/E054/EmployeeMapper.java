package org.apache.goat.chapter100.E054;


import org.apache.goat.common.Employee2;

import java.util.List;

public interface EmployeeMapper {

  //携带了哪个字段查询条件就带上这个字段的值
  public List<Employee2> getEmpsByConditionIf(Employee2 employee);

}
