

package org.apache.ibatis.submitted.discriminator;

public class Car extends Vehicle {
  protected Integer doorCount;

  public Integer getDoorCount() {
    return doorCount;
  }

  public void setDoorCount(Integer doorCount) {
    this.doorCount = doorCount;
  }
}
