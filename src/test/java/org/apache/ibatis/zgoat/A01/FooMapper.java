
package org.apache.ibatis.zgoat.A01;


import java.util.List;

public interface FooMapper {

  List<Foo> findAll();

  int deleteById(Integer id);

}
