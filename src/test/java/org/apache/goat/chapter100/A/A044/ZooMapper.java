
package org.apache.goat.chapter100.A.A044;


import org.apache.goat.common.Zoo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface ZooMapper {

  @Select("select * from zoo where id = #{id}")
  Zoo selectById(@Param("id") Integer id);

}
