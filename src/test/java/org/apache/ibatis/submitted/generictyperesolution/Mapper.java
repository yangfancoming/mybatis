
package org.apache.ibatis.submitted.generictyperesolution;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface Mapper {
  @Select("select * from users where id = #{id}")
  User getUser(User criteria);

  @Select("select * from users where name = #{name}")
  User getUserByName(String name);

  @Insert("insert into users (name, fld2) values (#{name}, #{fld2})")
  void insertUser(User user);
}
