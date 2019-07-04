
package org.apache.ibatis.submitted.lazyload_proxyfactory_comparison;

public class UserWithGetObjectWithInterface
implements Owned<Group> {

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

  @Override
  public Group getOwner() {
    return owner;
  }

  @Override
  public void setOwner(Group owner) {
    this.owner = owner;
  }

  public Object getObject() {
    return null;
  }
}
