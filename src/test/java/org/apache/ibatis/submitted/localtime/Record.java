
package org.apache.ibatis.submitted.localtime;

import java.time.LocalTime;

public class Record {

  private Integer id;

  private LocalTime t;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LocalTime getT() {
    return t;
  }

  public void setT(LocalTime t) {
    this.t = t;
  }
}
