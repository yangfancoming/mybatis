
package org.apache.ibatis.submitted.timezone_edge_case;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Record {

  private Integer id;

  private LocalDateTime ts;
  private LocalDate d;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LocalDateTime getTs() {
    return ts;
  }

  public void setTs(LocalDateTime ts) {
    this.ts = ts;
  }

  public LocalDate getD() {
    return d;
  }

  public void setD(LocalDate d) {
    this.d = d;
  }
}
