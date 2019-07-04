
package org.apache.ibatis.submitted.lazyload_proxyfactory_comparison;

public class UserWithGetXxxWithoutInterface {

  private Integer id;
  private String name;
  private Group owner;

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

  public Group getOwner() {
    return owner;
  }

  public void setOwner(Group owner) {
    this.owner = owner;
  }
}
