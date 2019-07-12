
package org.apache.ibatis.submitted.foreach_map;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapParam {

  private Map<Object, Object> map = new LinkedHashMap<>();

  public Map<Object, Object> getMap() {
    return map;
  }

  public void setMap(Map<Object, Object> map) {
    this.map = map;
  }


}
