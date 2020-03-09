package org.apache.goat.chapter100.C.C012;

import org.apache.goat.common.model.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface EmployeeMapper {

  public Employee getEmpByIdAndLastName1(@Param("id") Integer id);

  public Employee getEmpByIdAndLastName2(@Param("id") Integer id);

  public Employee getEmpByIdAndLastName3(@Param("id") Integer id, @Param("lastName") String lastName);

  public Employee getEmpByIdAndLastName4(Map<String, Object> map);

  public Employee getEmpByIdAndLastName5(Map<String, Object> map);

}
