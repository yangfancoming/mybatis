package org.apache.goat.chapter100.E034;


import org.apache.goat.common.Employee2;

import java.util.List;

public interface EmployeeMapper {

  public List<Employee2> getEmpsByDeptId(Integer deptId);
}
