
package org.apache.ibatis.submitted.timezone_edge_case;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select id, ts, d from records where id = #{id}")
  Record selectById(Integer id);

  @Insert("insert into records (id, ts, d) values (#{id}, #{ts}, #{d})")
  int insert(Record record);

}
