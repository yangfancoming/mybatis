
package org.apache.ibatis.submitted.generictyperesolution;

import java.io.Serializable;

public class Entity<T extends Serializable> {
  private T id;

  private String name;

  private T fld1;

  public T fld2;

  public T getId() {
    return id;
  }

  public void setId(T id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public T getFld1() {
    return fld1;
  }
}
