
package org.apache.ibatis.submitted.heavy_initial_load;

import org.apache.ibatis.annotations.Param;

public interface ThingMapper {

  Thing selectByCode(@Param("code") Code aCode);
}
