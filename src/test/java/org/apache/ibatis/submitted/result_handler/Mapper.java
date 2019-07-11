
package org.apache.ibatis.submitted.result_handler;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;


public interface Mapper {

  @Select("select * from users where id = #{value}")
  User getUser(Integer id);


  /**
   自定义 结果集处理器 作为参数的方法 返回值必须为void 否则  不会调用自定义处理器
  */
  @Select("select * from users")
  @ResultType(User.class)
//  List<User> getAllUsers(UserResultHandler resultHandler);
  void getAllUsers(UserResultHandler resultHandler);
}
