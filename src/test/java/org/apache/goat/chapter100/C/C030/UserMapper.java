package org.apache.goat.chapter100.C.C030;

import org.apache.goat.model.SysUser;

public interface UserMapper {

  /**
   * 新增用户
   * @param sysUser
   * @return
   */
  int insert(SysUser sysUser);

  int insert2(SysUser sysUser);
}
