
package org.apache.ibatis.submitted.lazy_properties;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface Mapper {
  User getUser(Integer id);

  @ResultMap("user")
  @Select("select 11 id, 'lazy1' name from (values(0))")
  User getLazy1();

  @ResultMap("user")
  @Select("select 12 id, 'lazy2' name from (values(0))")
  User getLazy2();

  @ResultMap("user")
  @Select("select 13 id, 'lazy3' name from (values(0))")
  List<User> getLazy3();
}
