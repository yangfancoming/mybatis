
package org.apache.goat.chapter200.A03;


import org.apache.goat.common.Zoo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 测试：接口Mapper内的方法能重载吗？  #101
 * 分析  #101  源码处  得出答案：
 * 1. Mapper接口中的方式 是不能重载的
 * 2. Mapper接口中的方法 和 对应的xml  sql 也不能重载
 * 例如： mapper接口中已方法
 *   @Select("select * from bar where id = #{id}")
 *   Zoo selectById(@Param("id") Integer id);
 *   则 对应的xml中 不能再有 重复的id   这样也算是重载  因为他们报错都是一样的
 *   <select id="selectById" parameterType="int"  resultType="org.apache.ibatis.zgoat.common.Zoo" >
 *     select * from zoo where id = #{id}
 *   </select>

*/
public interface ZooMapper {

  @Select("select * from bar where id = #{id}")
  Zoo selectById(@Param("id") Integer id);

  // Caused by: java.lang.IllegalArgumentException: Mapped Statements collection already contains value for org.apache.ibatis.zgoat.A03.ZooMapper.selectById.
//  @Select("select * from bar where id = #{id} and firstname = #{firstname}")
//  Zoo selectById(@Param("id") Integer id, @Param("firstname") String firstname);
}
