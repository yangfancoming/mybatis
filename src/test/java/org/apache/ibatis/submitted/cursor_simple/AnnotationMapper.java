
package org.apache.ibatis.submitted.cursor_simple;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;

public interface AnnotationMapper {

  @Select("select * from users order by id")
  Cursor<User> getAllUsers();

}
