
package org.apache.ibatis.submitted.default_method;

import org.apache.ibatis.annotations.Select;

interface PackageMapper {
  @Select("select * from users where id = #{id}")
  User getUserById(Integer id);

  default User defaultGetUser(Object... args) {
    return getUserById((Integer) args[0]);
  }
}
