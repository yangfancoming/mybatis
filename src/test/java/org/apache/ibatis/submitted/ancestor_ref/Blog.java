

package org.apache.ibatis.submitted.ancestor_ref;

public class Blog {
  private Integer id;
  private String title;
  private Author author;
  private Author coAuthor;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public Author getCoAuthor() {
    return coAuthor;
  }

  public void setCoAuthor(Author coAuthor) {
    this.coAuthor = coAuthor;
  }
}
