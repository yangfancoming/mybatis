package org.apache.goat.chapter100.C.C010;

import org.apache.goat.common.model.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface EmployeeMapper {

	public Employee getEmpById(Integer id);

  public Employee getEmpByIdAndLastName(Integer id,String lastName);

  public Employee getEmpByIdAndLastName1(Integer id,String lastName);

  public Employee getEmpByIdAndLastName2(Integer id,String lastName);

  public Employee getEmpByIdAndLastName3(@Param("id") Integer id,@Param("lastName") String lastName);

  public Employee getEmpByIdAndLastName4(Employee employee);

  public Employee getEmpByIdAndLastName5(Map<String, Object> map);

}
