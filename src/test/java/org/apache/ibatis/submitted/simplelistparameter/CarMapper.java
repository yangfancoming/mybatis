
package org.apache.ibatis.submitted.simplelistparameter;

import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CarMapper {

  @Select({ "select name from car where doors = #{doors[1]}" })
  List<Car> getCar(Car car);

  @Select({ "select name from car where doors = #{doors1[1]}" })
  List<Rv> getRv1(Rv rv);

  @Select({ "select name from car where doors = #{doors2[1]}" })
  List<Rv> getRv2(Rv rv);
}
