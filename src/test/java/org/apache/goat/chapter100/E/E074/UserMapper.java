package org.apache.goat.chapter100.E.E074;

import java.util.Map;

public interface UserMapper {

  /**
   * 通过 Map 更新列
   * @param map
   * @return
   */
  int updateByMap(Map<String, Object> map);
}
