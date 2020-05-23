package org.apache.goat.chapter100.E.E010;


import org.apache.goat.common.model.Employee;

public interface EmployeeMapper {

   Employee testWithResultMap(Integer id);

   Employee testWithNoResultMap(Integer id);
}
