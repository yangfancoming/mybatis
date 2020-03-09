package org.apache.goat.chapter100.E.E010;


import org.apache.goat.common.model.Employee;

public interface EmployeeMapper {

  public Employee getEmpById(Integer id);

  public Employee getEmpById2(Integer id);
}
