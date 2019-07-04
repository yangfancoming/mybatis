
package org.apache.ibatis.submitted.disallowdotsonnames;

import java.util.List;

public interface PersonMapper {
  Person selectByIdFlush(int id);

  Person selectByIdNoFlush(int id);

  List<Person> selectAllFlush();

  List<Person> selectAllNoFlush();
}
