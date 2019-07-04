
package org.apache.ibatis.submitted.named_constructor_args;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Select;

public interface UseActualNameMapper {

  @ConstructorArgs({
      @Arg(column = "name", name = "name"),
      @Arg(id = true, column = "id", name = "userId", javaType = Integer.class)
  })
  @Select("select * from users where id = #{id}")
  User mapConstructorWithoutParamAnnos(Integer id);

  User mapConstructorWithoutParamAnnosXml(Integer id);

}
