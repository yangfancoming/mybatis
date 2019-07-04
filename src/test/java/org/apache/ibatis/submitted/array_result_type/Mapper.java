
package org.apache.ibatis.submitted.array_result_type;

import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select * from users")
  User[] getUsers();

  User[] getUsersXml();

  @Select("select id from users")
  Integer[] getUserIds();

  @Select("select id from users")
  int[] getUserIdsPrimitive();
}
