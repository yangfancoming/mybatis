
package org.apache.ibatis.submitted.constructor_columnprefix;

public class Article {

  private EntityKey id;

  private String name;

  private Author author;

  private Author coauthor;

  public Article(EntityKey id, String name, Author author, Author coauthor) {
    super();
    this.id = id;
    this.name = name;
    this.author = author;
    this.coauthor = coauthor;
  }

  public EntityKey getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Author getAuthor() {
    return author;
  }

  public Author getCoauthor() {
    return coauthor;
  }
}
