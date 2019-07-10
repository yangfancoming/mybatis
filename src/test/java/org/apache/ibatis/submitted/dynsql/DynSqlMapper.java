
package org.apache.ibatis.submitted.dynsql;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DynSqlMapper {

  String selectDescription(@Param("p") String p);

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
