
package org.apache.ibatis.submitted.lazy_deserialize;

import org.apache.ibatis.annotations.Param;

/**
 * @since 2011-04-06T11:00:30+0200
 * @author Franta Mejta
 */
public interface Mapper {

  LazyObjectFoo loadFoo(@Param("fooId") int fooId);
}
