
package org.apache.goat.chapter100.G.G021;


import org.apache.goat.model.Foo;

import java.util.List;

public interface CacheLevel1Mapper {

  Foo findById(Integer id);

  List<Foo> findAll();

  int deleteById(Integer id);
}
