package org.apache.ibatis.binding;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 64274 on 2019/7/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/9---16:54
 */
public interface Mapper {

  @Insert("insert into param_test (id, size) values(#{id}, #{size})")
  void insert(@Param("id") String id, @Param("size") long size);

  @Insert("insert into param_test (id, size) values(#{id}, #{size})")
  void insertUsingHashMap(HashMap<String, Object> params);

  @Select("select size from param_test where id = #{id}")
  long selectSize(@Param("id") String id);

  @Select("select * from param_test")
  Map<String,Object> selectAll();
}
