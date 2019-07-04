

package org.apache.ibatis.submitted.empty_row;

public class Pet {
  private Integer id;
  private String name;

  private Pet grandchild;

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

  public Pet getGrandchild() {
    return grandchild;
  }

  public void setGrandchild(Pet grandchild) {
    this.grandchild = grandchild;
  }
}
