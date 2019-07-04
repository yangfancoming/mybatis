
package org.apache.ibatis.submitted.immutable_constructor;

import org.apache.ibatis.annotations.Param;

public interface ImmutablePOJOMapper {

  ImmutablePOJO getImmutablePOJO(@Param("pojoID") Integer pojoID);

  ImmutablePOJO getImmutablePOJONoMatchingConstructor(@Param("pojoID") Integer pojoID);

}
