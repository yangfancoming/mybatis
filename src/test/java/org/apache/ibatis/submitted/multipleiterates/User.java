
package org.apache.ibatis.submitted.multipleiterates;

public class User {

  private Integer id;

  private String name;

  private String[] firstAttr;

  private String[] secondAttr;

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

  public String[] getFirstAttr() {
    return firstAttr;
  }

  public void setFirstAttr(String[] firstAttr) {
    this.firstAttr = firstAttr;
  }

  public String[] getSecondAttr() {
    return secondAttr;
  }

  public void setSecondAttr(String[] secondAttr) {
    this.secondAttr = secondAttr;
  }
}
