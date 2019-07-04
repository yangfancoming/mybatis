

package org.apache.ibatis.submitted.discriminator;

public class Vehicle {
  protected Integer id;
  protected String maker;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getMaker() {
    return maker;
  }

  public void setMaker(String maker) {
    this.maker = maker;
  }
}
