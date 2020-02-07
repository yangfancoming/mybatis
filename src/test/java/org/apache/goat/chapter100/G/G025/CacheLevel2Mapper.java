
package org.apache.goat.chapter100.G.G025;


import org.apache.goat.model.Foo;

import java.util.List;

public interface CacheLevel2Mapper {

  List<Foo> findAll();

  int deleteById(Integer id);

}
