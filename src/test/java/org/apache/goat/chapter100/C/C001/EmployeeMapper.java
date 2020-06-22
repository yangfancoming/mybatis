package org.apache.goat.chapter100.C.C001;

import org.apache.goat.common.model.Employee;

 interface EmployeeMapper {

	 Employee getEmpById(Integer id);

	 Long addEmp(Employee employee);

	 boolean updateEmp(Employee employee);

	 Integer deleteEmpById(Integer id);
}
