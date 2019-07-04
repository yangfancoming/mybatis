
package org.apache.ibatis.submitted.resolution.javamethods;

import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.submitted.resolution.User;

@CacheNamespaceRef(MapperC.class)
public interface MapperB {
  @ResultMap("org.apache.ibatis.submitted.resolution.javamethods.MapperA.userRM")
  @Select("select * from users where id = #{id}")
  User getUser(Integer id);
}
