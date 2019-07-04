
package org.apache.ibatis.submitted.nested;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface Mapper {
  List<Map<String, Object>> simpleSelectWithMapperAndPrimitives(@Param("ids") int... values);
}
