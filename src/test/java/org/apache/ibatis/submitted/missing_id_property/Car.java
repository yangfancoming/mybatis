
package org.apache.ibatis.submitted.missing_id_property;

import java.util.List;

public class Car {
  // the result class doesn't need id for further processing
  private String name;
  private List<Part> carParts;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Part> getCarParts() {
    return carParts;
  }

  public void setCarParts(List<Part> carParts) {
    this.carParts = carParts;
  }
}
