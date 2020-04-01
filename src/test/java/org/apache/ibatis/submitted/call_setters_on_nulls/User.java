
package org.apache.ibatis.submitted.call_setters_on_nulls;

public class User {

  private Integer id;
  private String name;
  public boolean nullReceived;

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
    if (name == null) nullReceived = true;
    this.name = name;
  }
}
