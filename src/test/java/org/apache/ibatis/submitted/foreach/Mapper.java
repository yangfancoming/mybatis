
package org.apache.ibatis.submitted.foreach;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface Mapper {

  User getUser(User user);

  int countByUserList(List<User> users);

  int countByBestFriend(List<User> users);

  String selectWithNullItemCheck(List<User> users);

  int typoInItemProperty(List<User> users);

  int itemVariableConflict(@Param("id") Integer id, @Param("ids") List<Integer> ids, @Param("ids2") List<Integer> ids2);

  int indexVariableConflict(@Param("idx") Integer id, @Param("idxs") List<Integer> ids, @Param("idxs2") List<Integer> ids2);
}
