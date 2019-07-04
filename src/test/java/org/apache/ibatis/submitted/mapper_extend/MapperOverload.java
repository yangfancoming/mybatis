
package org.apache.ibatis.submitted.mapper_extend;

import org.apache.ibatis.annotations.Select;

public interface MapperOverload extends ParentMapper {

  @Override
  User getUserXML();

  @Override
  @Select("select * from users where id = 2")
  User getUserAnnotated();

}
