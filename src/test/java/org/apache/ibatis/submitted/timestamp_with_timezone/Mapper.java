
package org.apache.ibatis.submitted.timestamp_with_timezone;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select id, odt, odt ot from records where id = #{id}")
  Record selectById(Integer id);

  @Insert("insert into records (id, odt) values (#{id}, #{odt})")
  int insertOffsetDateTime(Record record);

  @Insert("insert into records (id, odt) values (#{id}, #{ot})")
  int insertOffsetTime(Record record);

}
