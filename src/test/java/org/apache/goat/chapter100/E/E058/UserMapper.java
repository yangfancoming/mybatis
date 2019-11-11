package org.apache.goat.chapter100.E.E058;

import org.apache.goat.model.SysUser;

public interface UserMapper {

  /**
   * 根据用户 id 或用户名查询
   *
   * @param sysUser
   * @return
   */
  SysUser selectByIdOrUserName(SysUser sysUser);
}
