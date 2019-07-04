
package org.apache.ibatis.submitted.batch_test;

public interface Mapper {

  User getUser(Integer id);

  Dept getDept(Integer id);

  void insertUser(User user);
}
