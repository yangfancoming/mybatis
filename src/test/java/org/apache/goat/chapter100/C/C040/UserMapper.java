package org.apache.goat.chapter100.C.C040;

import org.apache.goat.model.SysUser;

public interface UserMapper {

  /**
   * 根据主键更新
   *
   * @param sysUser
   * @return
   */
  int updateById(SysUser sysUser);

  /**
   * 通过 id 查询用户
   *
   * @param id
   * @return
   */
  SysUser selectById(Long id);
}
