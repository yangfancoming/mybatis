
package org.apache.goat.chapter100.B.B060;



import org.apache.goat.common.Foo;

import java.util.List;

public interface FooMapper {

  Foo selectById(Integer id);

  List<Foo> findAll();

  int deleteById(Integer id);
}