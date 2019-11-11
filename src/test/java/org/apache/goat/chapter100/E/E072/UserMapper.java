package org.apache.goat.chapter100.E.E072;

import org.apache.goat.model.SysUser;

import java.util.List;

public interface UserMapper {

  /**
   * 批量插入用户信息
   *
   * @param userList
   * @return
   */
  int insertList(List<SysUser> userList);

}
