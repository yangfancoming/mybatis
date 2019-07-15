
package org.apache.ibatis.submitted.dynsql;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DynSqlMapper {

  List<Map> selectDescription(@Param("p") String p);

//  List<Map> selectWhere(Integer id,String description);

  List<String> selectDescriptionById(Integer id);

  List<String> selectDescriptionByConditions(Conditions conditions);
  List<String> selectDescriptionByConditions2(Conditions conditions);
  List<String> selectDescriptionByConditions3(Conditions conditions);

  class Conditions {
    private Integer id;

    public void setId(Integer id) {
      this.id = id;
    }

    public Integer getId() {
      return id;
    }
  }

}
