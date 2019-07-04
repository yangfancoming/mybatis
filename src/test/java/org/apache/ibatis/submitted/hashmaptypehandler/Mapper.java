
package org.apache.ibatis.submitted.hashmaptypehandler;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select * from users where id = #{id} and name = #{name}")
  User getUser(@Param("id") Integer id, @Param("name") String name);

  User getUserXml(@Param("id") Integer id, @Param("name") String name);

  @Select("select * from users where name = #{map}")
  User getUserWithTypeHandler(HashMap<String, String> map);

  User getUserWithTypeHandlerXml(HashMap<String, String> map);

}
