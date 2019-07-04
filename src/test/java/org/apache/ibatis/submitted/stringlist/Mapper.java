
package org.apache.ibatis.submitted.stringlist;

import java.util.List;
import java.util.Map;

public interface Mapper {

  List<User> getUsersAndGroups(Integer id);

  List<Map<String, Object>> getUsersAndGroupsMap(Integer id);

}
