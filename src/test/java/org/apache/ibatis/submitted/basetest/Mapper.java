
package org.apache.ibatis.submitted.basetest;

public interface Mapper {

  User getUser(Integer id);

  void insertUser(User user);

}
