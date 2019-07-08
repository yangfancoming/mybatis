
package org.apache.ibatis.zgoat.A02;



import java.util.List;

public interface FooMapper {

  Foo selectById(Integer id);

  List<Foo> findAll();

  int deleteById(Integer id);
}
