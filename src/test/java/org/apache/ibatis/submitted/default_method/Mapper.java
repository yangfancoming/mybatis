
package org.apache.ibatis.submitted.default_method;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select * from users where id = #{id}")
  User getUserById(Integer id);

  @Select("select * from users where id = #{id} and name = #{name}")
  User getUserByIdAndName(@Param("name") String name, @Param("id") Integer id);

  default User defaultGetUser(Object... args) {
    return getUserById((Integer) args[0]);
  }

}
