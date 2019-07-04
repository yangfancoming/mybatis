
package org.apache.ibatis.submitted.result_set_type;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface Mapper {

  List<User> getUserWithStatementAndUnset(RowBounds rowBounds);
  List<User> getUserWithStatementAndDefault(RowBounds rowBounds);
  List<User> getUserWithStatementAndForwardOnly(RowBounds rowBounds);
  List<User> getUserWithStatementAndScrollInsensitive(RowBounds rowBounds);
  List<User> getUserWithStatementAndScrollSensitive(RowBounds rowBounds);

  List<User> getUserWithPreparedAndUnset(RowBounds rowBounds);
  List<User> getUserWithPreparedAndDefault(RowBounds rowBounds);
  List<User> getUserWithPreparedAndForwardOnly(RowBounds rowBounds);
  List<User> getUserWithPreparedAndScrollInsensitive(RowBounds rowBounds);
  List<User> getUserWithPreparedAndScrollSensitive(RowBounds rowBounds);

  List<User> getUserWithCallableAndUnset(RowBounds rowBounds);
  List<User> getUserWithCallableAndDefault(RowBounds rowBounds);
  List<User> getUserWithCallableAndForwardOnly(RowBounds rowBounds);
  List<User> getUserWithCallableAndScrollInsensitive(RowBounds rowBounds);
  List<User> getUserWithCallableAndScrollSensitive(RowBounds rowBounds);

}
