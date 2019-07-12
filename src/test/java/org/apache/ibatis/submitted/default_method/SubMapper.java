package org.apache.ibatis.submitted.default_method;

/**
 * Created by 64274 on 2019/7/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/12---11:56
 */
public interface SubMapper extends Mapper {

  default User defaultGetUser(Object... args) {
    return getUserByIdAndName((String) args[0], (Integer) args[1]);
  }

}
