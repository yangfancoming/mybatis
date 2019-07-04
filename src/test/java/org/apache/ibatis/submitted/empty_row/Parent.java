
package org.apache.ibatis.submitted.empty_row;

import java.util.List;

public class Parent {
  private Integer id;
  private String col1;
  private String col2;

  private Child child;
  private List<Child> children;
  private List<Pet> pets;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCol1() {
    return col1;
  }

  public void setCol1(String col1) {
    this.col1 = col1;
  }

  public String getCol2() {
    return col2;
  }

  public void setCol2(String col2) {
    this.col2 = col2;
  }

  public Child getChild() {
    return child;
  }

  public void setChild(Child child) {
    this.child = child;
  }

  public List<Child> getChildren() {
    return children;
  }

  public void setChildren(List<Child> children) {
    this.children = children;
  }

  public List<Pet> getPets() {
    return pets;
  }

  public void setPets(List<Pet> pets) {
    this.pets = pets;
  }
}
