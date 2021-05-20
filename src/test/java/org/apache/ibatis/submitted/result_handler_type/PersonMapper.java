
package org.apache.ibatis.submitted.result_handler_type;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface PersonMapper {

  List<Person> doSelect();

  @Select("select * from person")
  @MapKey("id")
  Map<Integer, Person> selectAsMap();
}
