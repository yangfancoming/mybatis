
package org.apache.ibatis.submitted.postgres_genkeys;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface Mapper {
  @Insert("insert into mbtest.sections (section_id, name) values (#{sectionId}, #{name})")
  int insertSection(Section section);

  @Update("update mbtest.users set name = #{name} where user_id = #{userId}")
  int updateUser(User user);

  @Insert("insert into mbtest.users (name) values (#{name})")
  int insertUser(@Param("name") String name);
}
