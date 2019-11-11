package org.apache.goat.chapter100.E.E062;

import org.apache.goat.model.SysUser;

import java.util.List;

public interface UserMapper {
  /**
   * 根据动态条件查询用户信息
   *
   * @param sysUser
   * @return
   */
  List<SysUser> selectByUser(SysUser sysUser);
}
