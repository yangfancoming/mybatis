
package org.apache.goat.chapter100.A.A002;


import org.apache.goat.common.model.Foo;
import org.apache.ibatis.annotations.Select;


public interface FooMapper2 {

  @Select("select * from foo where id = #{id}")
  Foo selectById(Integer id);

}
