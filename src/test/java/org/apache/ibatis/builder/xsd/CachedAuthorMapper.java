
package org.apache.ibatis.builder.xsd;

import org.apache.ibatis.domain.blog.Author;

public interface CachedAuthorMapper {
    Author selectAllAuthors();
    Author selectAuthorWithInlineParams(int id);
    void insertAuthor(Author author);
    boolean updateAuthor(Author author);
    boolean deleteAuthor(int id);
}
