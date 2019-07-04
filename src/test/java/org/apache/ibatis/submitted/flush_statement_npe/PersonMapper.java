
package org.apache.ibatis.submitted.flush_statement_npe;

public interface PersonMapper {
  Person selectById(int id);

  void update(Person person);
}
