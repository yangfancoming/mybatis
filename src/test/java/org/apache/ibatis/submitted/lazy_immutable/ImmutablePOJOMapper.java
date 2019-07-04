
package org.apache.ibatis.submitted.lazy_immutable;

import org.apache.ibatis.annotations.Param;

public interface ImmutablePOJOMapper {

  ImmutablePOJO getImmutablePOJO(@Param("pojoID") Integer pojoID);

}
