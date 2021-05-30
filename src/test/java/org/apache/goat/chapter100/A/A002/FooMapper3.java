
package org.apache.goat.chapter100.A.A002;


import org.apache.ibatis.annotations.Delete;


public interface FooMapper3 {

  @Delete("delete from foo where id = #{id}")
  int deleteById(Integer id);
}
