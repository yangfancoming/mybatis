
package org.apache.common.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.domain.blog.Blog;

public interface BlogMapper {

  @Select("select * from Blog where id = #{id}")
  Blog selectBlog(int id);

}
