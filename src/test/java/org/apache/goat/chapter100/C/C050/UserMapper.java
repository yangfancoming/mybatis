package org.apache.goat.chapter100.C.C050;

import org.apache.goat.model.SysUser;

public interface UserMapper {

  int deleteById(Long id);

  int deleteById(SysUser sysUser);

  SysUser selectById(Long id);
}
