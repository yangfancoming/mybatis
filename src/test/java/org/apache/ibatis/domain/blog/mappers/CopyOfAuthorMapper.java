
package org.apache.ibatis.domain.blog.mappers;

import java.util.List;

import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.session.ResultHandler;

public interface CopyOfAuthorMapper {

  List selectAllAuthors();

  void selectAllAuthors(ResultHandler handler);

  Author selectAuthor(int id);

  void selectAuthor(int id, ResultHandler handler);

  void insertAuthor(Author author);

  int deleteAuthor(int id);

  int updateAuthor(Author author);

}
