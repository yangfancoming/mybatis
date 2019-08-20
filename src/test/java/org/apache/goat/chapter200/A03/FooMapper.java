
package org.apache.goat.chapter200.A03;



import org.apache.goat.chapter200.common.Foo;

import java.util.List;

public interface FooMapper {

  Foo selectById(Integer id);

  Foo selectById1(Integer id);

  List<Foo> findAll();

  int deleteById(Integer id);

}
