package org.apache.ibatis.binding.item01;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 64274 on 2019/7/9.
 * @ Description: 查询参数  参数映射  方法参数
 * @ author  山羊来了
 * @ date 2019/7/9---16:54
 */
public interface Mapper {

  @Insert("insert into param_test (id, size) values(#{id}, #{size})")
  int insert(@Param("id") String id, @Param("size") long size);

  @Insert("insert into param_test (id, size) values(#{id}, #{size})")
  void insertUsingHashMap(HashMap<String, Object> params);

  // 走 @Param  注解的情况
  @Select("select size from param_test where id = #{id}")
  Long selectById1(@Param("id") String id);

  // 没有 @Param  注解的情况
  @Select("select size from param_test where id = #{id}")
  Long selectById2(String id);

  @Select("select * from param_test")
  Map<String,Object> selectAll();


  @Select("select * from param_test")
  List selectList();
}
