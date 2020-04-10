
package org.apache.goat.chapter100.B.B062;



import org.apache.goat.common.model.Foo;
import org.apache.ibatis.annotations.Delete;


public interface FooMapper {

  Foo selectById(Integer id);

//  @Delete("delete from foo where id = #{id}")
  int deleteById(Integer id);

}
