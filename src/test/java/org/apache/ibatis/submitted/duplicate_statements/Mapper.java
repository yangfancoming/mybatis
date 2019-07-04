
package org.apache.ibatis.submitted.duplicate_statements;

import java.util.List;

import org.apache.ibatis.session.RowBounds;

public interface Mapper {

  List<User> getAllUsers();

  List<User> getAllUsers(RowBounds rowBounds);
}
