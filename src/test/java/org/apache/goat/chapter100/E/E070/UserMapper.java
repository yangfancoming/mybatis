package org.apache.goat.chapter100.E.E070;

import org.apache.goat.model.SysUser;

import java.util.List;

public interface UserMapper {

  /**
   * 根据用户 id 集合查询
   *
   * @param idList
   * @return
   */
  List<SysUser> selectByIdList(List<Long> idList);

}
