
package org.apache.ibatis.submitted.resolution.javamethods;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.submitted.resolution.User;

@CacheNamespace
public interface MapperC {
  @ResultMap("org.apache.ibatis.submitted.resolution.javamethods.MapperA.userRM")
  @Select("select * from users where id = #{id}")
  User getUser(Integer id);
}
