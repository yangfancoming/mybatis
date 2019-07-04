
package org.apache.ibatis.submitted.localtime;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface Mapper {

  @Select("select id, t from records where id = #{id}")
  Record selectById(Integer id);

  @Insert("insert into records (id, t) values (#{id}, #{t})")
  int insertLocalTime(Record record);

}
