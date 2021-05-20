
package org.apache.ibatis.submitted.maptypehandler;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface Mapper {

  @Select("select * from users where id = #{id}")
  User getUser(@Param("id") Integer id, @Param("name") String name);

  @Select("select * from users where id = #{id} and name = #{name}")
  User getUserFromAMap(Map<String, Object> params);

  User getUserXML(Map<String, Object> params);

}
