
package org.apache.ibatis.submitted.cursor_nested;

import org.apache.ibatis.cursor.Cursor;

public interface Mapper {

  Cursor<User> getAllUsers();

}
