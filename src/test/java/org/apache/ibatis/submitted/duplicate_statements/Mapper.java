
package org.apache.ibatis.submitted.duplicate_statements;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface Mapper {

  List<User> getAllUsers();

  List<User> getAllUsers(RowBounds rowBounds);
}
