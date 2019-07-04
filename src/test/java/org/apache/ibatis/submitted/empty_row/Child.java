

package org.apache.ibatis.submitted.empty_row;

public class Child {
  private Integer id;
  private String name;

  private Child grandchild;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Child getGrandchild() {
    return grandchild;
  }

  public void setGrandchild(Child grandchild) {
    this.grandchild = grandchild;
  }
}
