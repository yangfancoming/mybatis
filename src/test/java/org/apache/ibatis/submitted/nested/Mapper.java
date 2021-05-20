
package org.apache.ibatis.submitted.nested;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface Mapper {
  List<Map<String, Object>> simpleSelectWithMapperAndPrimitives(@Param("ids") int... values);
}
