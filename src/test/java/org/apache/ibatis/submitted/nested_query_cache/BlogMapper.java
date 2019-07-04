
package org.apache.ibatis.submitted.nested_query_cache;

import org.apache.ibatis.domain.blog.Blog;

public interface BlogMapper {

  Blog selectBlog(int id);

  Blog selectBlogUsingConstructor(int id);

}
