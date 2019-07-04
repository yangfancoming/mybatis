
package org.apache.ibatis.submitted.cacheorder;

import java.io.Serializable;

public class User implements Serializable {

  private static final long serialVersionUID = 2636291819488700444L;
  private Integer id;
  private String name;

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
