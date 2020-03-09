package org.apache.goat.chapter100.E.E004;

import org.apache.goat.common.model.Employee;
import org.apache.ibatis.annotations.MapKey;

import java.util.Map;

public interface EmployeeMapper {


  public Map<String, Object> getEmpByIdReturnMap(Integer id);



  //@MapKey:告诉mybatis封装这个map的时候使用哪个属性作为map的key
  @MapKey("id")
  public Map<String, Employee> getEmpByLastNameLikeReturnMap(String lastName);
}
