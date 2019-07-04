
package org.apache.ibatis.submitted.resolution.cacherefs;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.submitted.resolution.User;

@CacheNamespace
public interface MapperB {
  User getUser(Integer id);
}
