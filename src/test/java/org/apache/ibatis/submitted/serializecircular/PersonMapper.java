
package org.apache.ibatis.submitted.serializecircular;

import java.util.List;

public interface PersonMapper {
  Person getById(Integer anId);

  List<Person> getByDepartment(Integer anDepartmentId);
}
