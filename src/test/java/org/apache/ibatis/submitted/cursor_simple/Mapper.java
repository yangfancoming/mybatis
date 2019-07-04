
package org.apache.ibatis.submitted.cursor_simple;

import org.apache.ibatis.cursor.Cursor;

public interface Mapper {

  Cursor<User> getAllUsers();

}
