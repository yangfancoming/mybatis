

package org.apache.ibatis.submitted.results_id;

import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

public interface AnotherMapper {

  @ResultMap("org.apache.ibatis.submitted.results_id.Mapper.userResult")
  @Select("select * from users order by uid")
  List<User> getUsers();

  User getUser(Integer id);

}
