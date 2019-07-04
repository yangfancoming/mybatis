
package org.apache.ibatis.submitted.rounding;

public interface Mapper {

  User getUser(Integer id);

  void insert(User user);

  User getUser2(Integer id);

  void insert2(User user);

}
