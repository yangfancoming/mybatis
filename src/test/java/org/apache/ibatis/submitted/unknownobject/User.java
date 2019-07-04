
package org.apache.ibatis.submitted.unknownobject;

public class User {

  private Integer id;
  private String name;
  private UnknownObject unknownObject;

  public UnknownObject getUnknownObject() {
    return unknownObject;
  }

  public void setUnknownObject(UnknownObject unknownObject) {
    this.unknownObject = unknownObject;
  }

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
}
