package org.apache.goat.chapter100.E.E064;

import org.apache.goat.model.SysUser;


public interface UserMapper {
  int updateByIdSelective(SysUser sysUser);
}
