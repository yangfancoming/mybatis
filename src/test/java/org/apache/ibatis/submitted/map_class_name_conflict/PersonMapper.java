
package org.apache.ibatis.submitted.map_class_name_conflict;

public interface PersonMapper {

  Person get(Long id);

  void insert(Person person);
}
