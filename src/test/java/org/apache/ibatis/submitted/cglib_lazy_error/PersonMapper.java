
package org.apache.ibatis.submitted.cglib_lazy_error;

public interface PersonMapper {

  Person selectById(int id);

  Person selectByStringId(String id);

  int insertPerson(Person person);

}
