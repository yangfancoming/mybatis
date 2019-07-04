
package org.apache.ibatis.domain.blog.mappers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.session.ResultHandler;

public interface AuthorMapper {

  List<Author> selectAllAuthors();

  Set<Author> selectAllAuthorsSet();

  Vector<Author> selectAllAuthorsVector();

  LinkedList<Author> selectAllAuthorsLinkedList();

  Author[] selectAllAuthorsArray();

  void selectAllAuthors(ResultHandler handler);

  Author selectAuthor(int id);

  LinkedHashMap<String, Object> selectAuthorLinkedHashMap(int id);

  void selectAuthor(int id, ResultHandler handler);

  @Select("select")
  void selectAuthor2(int id, ResultHandler handler);

  void insertAuthor(Author author);

  int deleteAuthor(int id);

  int updateAuthor(Author author);

}
