package org.apache.goat.chapter100.E.E001;

import org.apache.goat.common.model.Employee;

import java.util.List;

public interface EmployeeMapper {

  public List<Employee> getEmpsByLastNameLike(String lastName);

}
