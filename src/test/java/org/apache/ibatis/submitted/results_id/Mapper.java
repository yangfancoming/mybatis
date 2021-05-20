
package org.apache.ibatis.submitted.results_id;

import org.apache.ibatis.annotations.*;

public interface Mapper {

  @Results(id = "userResult", value = {
    @Result(id = true, column = "uid", property = "id"),
    @Result(column = "name", property = "name")
  })
  @Select("select * from users where uid = #{id}")
  User getUserById(Integer id);

  @ResultMap("userResult")
  @Select("select * from users where name = #{name}")
  User getUserByName(String name);

  @Results(id = "userResultConstructor")
  @ConstructorArgs({
    @Arg(id = true, column = "uid", javaType = Integer.class),
    @Arg(column = "name", javaType = String.class)
  })
  @Select("select * from users where uid = #{id}")
  User getUserByIdConstructor(Integer id);

  @ResultMap("userResultConstructor")
  @Select("select * from users where name = #{name}")
  User getUserByNameConstructor(String name);
}
