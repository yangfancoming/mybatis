
package org.apache.ibatis.submitted.duplicate_resource_loaded;

import java.util.List;
import java.util.Map;

public interface Mapper {

  List<Map<String, Object>> selectAllBlogs();

}
