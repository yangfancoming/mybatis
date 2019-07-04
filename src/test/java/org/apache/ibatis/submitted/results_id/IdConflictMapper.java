
package org.apache.ibatis.submitted.results_id;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface IdConflictMapper {

  @Results(id = "userResult", value = { @Result(id = true, column = "uid", property = "id") })
  @Select("select * from users where uid = #{id}")
  User getUserById(Integer id);
}
