
package org.apache.ibatis.submitted.use_actual_param_name;

import java.util.List;

import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select * from users where id = #{foo}")
  User getUserById(Integer id);

  @Select("select * from users where id = #{id} and name = #{name}")
  User getUserByIdAndName(Integer id, String name);

  List<User> getUsersByIdList(List<Integer> ids);

  List<User> getUsersByIdListAndName(List<Integer> ids, String name);

}
