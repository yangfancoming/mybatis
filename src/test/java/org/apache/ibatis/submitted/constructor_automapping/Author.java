
package org.apache.ibatis.submitted.constructor_automapping;

public class Author {

  private final Integer id;
  private String name;

  private Author(Integer id) {
    super();
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
