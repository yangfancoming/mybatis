package org.apache.goat.chapter100.E004;

import org.apache.goat.common.Employee;

import java.util.List;

public interface EmployeeMapper {

  public List<Employee> getEmpsByLastNameLike(String lastName);

}
