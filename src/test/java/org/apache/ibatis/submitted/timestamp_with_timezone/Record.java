
package org.apache.ibatis.submitted.timestamp_with_timezone;

import java.time.OffsetDateTime;
import java.time.OffsetTime;

public class Record {

  private Integer id;

  private OffsetDateTime odt;

  private OffsetTime ot;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public OffsetDateTime getOdt() {
    return odt;
  }

  public void setOdt(OffsetDateTime odt) {
    this.odt = odt;
  }

  public OffsetTime getOt() {
    return ot;
  }

  public void setOt(OffsetTime ot) {
    this.ot = ot;
  }

}
