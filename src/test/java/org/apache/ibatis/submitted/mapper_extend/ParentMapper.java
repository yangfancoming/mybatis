
package org.apache.ibatis.submitted.mapper_extend;

import org.apache.ibatis.annotations.Select;

public interface ParentMapper extends GrandpaMapper {

  User getUserXML();

  @Select("select * from users where id = 1")
  User getUserAnnotated();

}
