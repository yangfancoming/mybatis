package org.apache.goat.chapter100.E038;


import org.apache.goat.common.Employee2;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeMapper {

  public List<Employee2> getEmpsByDeptId(@Param("deptId") Integer deptId,@Param("lastName") String lastName);
}
