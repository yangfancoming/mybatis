
package org.apache.goat.chapter100.A042;



import org.apache.goat.common.Foo;


public interface FooMapper {

//  @Select("select * from foo where id = #{id}")
  Foo selectById(Integer id);

}
