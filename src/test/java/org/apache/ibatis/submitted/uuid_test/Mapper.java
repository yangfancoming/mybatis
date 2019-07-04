
package org.apache.ibatis.submitted.uuid_test;

import java.util.UUID;

public interface Mapper {

  User getUser(UUID id);

  void insertUser(User user);

}
