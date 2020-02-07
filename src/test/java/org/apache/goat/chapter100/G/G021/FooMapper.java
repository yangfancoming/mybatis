
package org.apache.goat.chapter100.G.G021;


import java.util.List;

public interface FooMapper {

  List<Foo> findAll();

  int deleteById(Integer id);

}
