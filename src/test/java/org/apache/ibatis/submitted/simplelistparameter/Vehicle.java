
package org.apache.ibatis.submitted.simplelistparameter;

import java.util.List;

public class Vehicle<T> {
  public T name;

  public List<T> doors1;

  private List<T> doors2;

  public List<T> getDoors2() {
    return doors2;
  }

  public void setDoors2(List<T> doors2) {
    this.doors2 = doors2;
  }
}
