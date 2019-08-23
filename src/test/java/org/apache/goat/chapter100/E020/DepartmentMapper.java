package org.apache.goat.chapter100.E020;


import org.apache.goat.common.Department;

public interface DepartmentMapper {
	
	public Department getDeptById(Integer id);
	
	public Department getDeptByIdPlus(Integer id);

	public Department getDeptByIdStep(Integer id);
}
