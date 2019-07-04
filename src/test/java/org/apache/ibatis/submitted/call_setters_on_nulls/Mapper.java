
package org.apache.ibatis.submitted.call_setters_on_nulls;

import java.util.List;
import java.util.Map;

public interface Mapper {

  User getUserMapped(Integer id);

  User getUserUnmapped(Integer id);

  Map getUserInMap(Integer id);

  List<Map<String, Object>> getNameOnly();

  List<Map<String, Object>> getNameOnlyMapped();

}
