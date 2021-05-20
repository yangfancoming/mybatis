
package org.apache.ibatis.submitted.blocking_cache;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@CacheNamespace(blocking = true)
public interface PersonMapper {

  @Select("select id, firstname, lastname from person")
  List<Person> findAll();
}
