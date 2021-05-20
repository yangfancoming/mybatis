
package org.apache.ibatis.binding;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.domain.blog.Blog;

import java.util.List;

public interface MapperWithOneAndMany {

  @Select({ "SELECT *","FROM blog" })
  @Results({
    @Result(
       property = "author", column = "author_id",
       one = @One(select = "org.apache.ibatis.binding.BoundAuthorMapper.selectAuthor"),
       many = @Many(select = "selectPostsById"))
  })
  List<Blog> selectWithBothOneAndMany();
}
