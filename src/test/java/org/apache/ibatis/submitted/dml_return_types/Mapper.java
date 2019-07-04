
package org.apache.ibatis.submitted.dml_return_types;

public interface Mapper {

  User getUser(int id);

  void updateReturnsVoid(User user);

  int updateReturnsPrimitiveInteger(User user);

  Integer updateReturnsInteger(User user);

  long updateReturnsPrimitiveLong(User user);

  Long updateReturnsLong(User user);

  boolean updateReturnsPrimitiveBoolean(User user);

  Boolean updateReturnsBoolean(User user);

}
