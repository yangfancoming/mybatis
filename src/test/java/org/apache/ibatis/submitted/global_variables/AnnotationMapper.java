
package org.apache.ibatis.submitted.global_variables;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;

@CacheNamespace(implementation = CustomCache.class, properties = {
    @Property(name = "stringValue", value = "${stringProperty}"),
    @Property(name = "integerValue", value = "${integerProperty}"),
    @Property(name = "longValue", value = "${longProperty}")
})
public interface AnnotationMapper {

  @Select("select * from ${table} where id = #{id}")
  User getUser(Integer id);

}
