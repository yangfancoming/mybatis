

package org.apache.ibatis.submitted.discriminator;

public class Truck extends Vehicle {
  protected Float carryingCapacity;

  public Float getCarryingCapacity() {
    return carryingCapacity;
  }

  public void setCarryingCapacity(Float carryingCapacity) {
    this.carryingCapacity = carryingCapacity;
  }
}
