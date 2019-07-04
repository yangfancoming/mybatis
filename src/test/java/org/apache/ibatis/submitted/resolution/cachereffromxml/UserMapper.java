
package org.apache.ibatis.submitted.resolution.cachereffromxml;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.submitted.resolution.User;

@CacheNamespace
public interface UserMapper {
  User getUser(Integer id);
}
