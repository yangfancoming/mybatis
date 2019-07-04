
package org.apache.ibatis.submitted.column_forwarding;

import org.apache.ibatis.annotations.Param;

public interface Mapper {
  User getUser(@Param("id") int id);
}
