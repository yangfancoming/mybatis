
package org.apache.goat.chapter100.A.A003;


import org.apache.goat.common.model.Foo;


public interface FooMapper {

  Foo selectById(Integer id);

  Foo selectById(String id);

}
