
package org.apache.ibatis.submitted.enum_interface_type_handler;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface Mapper {
  @Select("select * from users where id = #{id}")
  User getUser(Integer id);

  @Insert("insert into users (id, color) values (#{id}, #{color})")
  int insertUser(User user);
}
