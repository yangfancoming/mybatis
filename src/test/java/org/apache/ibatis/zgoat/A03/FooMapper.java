
package org.apache.ibatis.zgoat.A03;


import org.apache.ibatis.zgoat.common.Foo;
import java.util.List;

public interface FooMapper {

  Foo selectById(Integer id);

  List<Foo> findAll();

  int deleteById(Integer id);
}
