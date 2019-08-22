package org.apache.goat.chapter100.C001;

import org.apache.goat.common.Employee;

public interface EmployeeMapper {


	
	public Employee getEmpById(Integer id);

	public Long addEmp(Employee employee);

	public boolean updateEmp(Employee employee);

	public void deleteEmpById(Integer id);
	
}
