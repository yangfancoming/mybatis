
package org.apache.ibatis.submitted.duplicate_statements;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

/**
 * This interface should be OK.  It has duplicate method names, but only
 * because of the RowBounds parameter
 *
 */
public interface AnnotatedMapper {

  @Select("select * from users")
  List<User> getAllUsers();

  @Select("select * from users")
  List<User> getAllUsers(RowBounds rowBounds);
}
