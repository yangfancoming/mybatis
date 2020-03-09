
package org.apache.goat.chapter100.A.A042;


import org.apache.goat.common.model.Zoo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface ZooMapper {

  @Select("select * from zoo where id = #{id}")
  Zoo selectById(@Param("id") Integer id);

}
