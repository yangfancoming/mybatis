
package org.apache.ibatis.submitted.sqlprovider;

public class User {
  @BaseMapper.Column
  private Integer id;
  @BaseMapper.Column
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
