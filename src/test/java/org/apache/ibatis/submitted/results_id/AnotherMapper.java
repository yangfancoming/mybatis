

package org.apache.ibatis.submitted.results_id;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AnotherMapper {

  @ResultMap("org.apache.ibatis.submitted.results_id.Mapper.userResult")
  @Select("select * from users order by uid")
  List<User> getUsers();

  User getUser(Integer id);

}
