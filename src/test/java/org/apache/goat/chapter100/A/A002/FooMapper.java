
package org.apache.goat.chapter100.A.A002;


import org.apache.goat.common.model.Foo;

import java.util.List;

public interface FooMapper {

  Foo selectById(Integer id);

  List<Foo> findAll();

  int deleteById(Integer id);
}
