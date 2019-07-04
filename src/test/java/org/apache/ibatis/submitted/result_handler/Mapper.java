
package org.apache.ibatis.submitted.result_handler;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select * from users where id = #{value}")
  User getUser(Integer id);

  @Select("select * from users")
  @ResultType(User.class)
  void getAllUsers(UserResultHandler resultHandler);
}
