
package org.apache.ibatis.submitted.rounding;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class User {

  private Integer id;
  private String name;
  private BigDecimal funkyNumber;
  private RoundingMode roundingMode;

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

  public BigDecimal getFunkyNumber() {
    return funkyNumber;
  }

  public void setFunkyNumber(BigDecimal big) {
    funkyNumber = big;
  }

  public RoundingMode getRoundingMode() {
    return roundingMode;
  }

  public void setRoundingMode(RoundingMode mode) {
    roundingMode = mode;
  }

}
