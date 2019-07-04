
package org.apache.ibatis.submitted.array_type_handler;

public interface Mapper {

  void insert(User user);

  int getUserCount();

  /**
   * HSQL returns NULL when asked for the cardinality of an array column with NULL value :-(
   */
  Integer getNicknameCount();
}
