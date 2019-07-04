
package org.apache.ibatis.submitted.nested_query_cache;

import org.apache.ibatis.domain.blog.Author;

public interface AuthorMapper {

  Author selectAuthor(int id);

}
