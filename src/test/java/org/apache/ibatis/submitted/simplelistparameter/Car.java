
package org.apache.ibatis.submitted.simplelistparameter;

import java.util.List;

public class Car {
  private String name;
  private List<String> doors;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getDoors() {
    return doors;
  }

  public void setDoors(List<String> doors) {
    this.doors = doors;
  }
}
