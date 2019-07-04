
package org.apache.ibatis.submitted.lazyload_proxyfactory_comparison;

import org.apache.ibatis.annotations.Param;

public interface Mapper {
    UserWithGetObjectWithInterface getUserWithGetObjectWithInterface(@Param("id") Integer id);
    UserWithGetObjectWithoutInterface getUserWithGetObjectWithoutInterface(@Param("id") Integer id);

    UserWithGetXxxWithInterface getUserWithGetXxxWithInterface(@Param("id") Integer id);
    UserWithGetXxxWithoutInterface getUserWithGetXxxWithoutInterface(@Param("id") Integer id);

    UserWithNothingWithInterface getUserWithNothingWithInterface(@Param("id") Integer id);
    UserWithNothingWithoutInterface getUserWithNothingWithoutInterface(@Param("id") Integer id);
}
