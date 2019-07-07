
package org.apache.ibatis.submitted.cache;

import java.util.List;

public interface FooMapper {

  List<Foo> findAll();

  int deleteById(Integer id);

}
