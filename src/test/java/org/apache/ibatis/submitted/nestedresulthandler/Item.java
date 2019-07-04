
package org.apache.ibatis.submitted.nestedresulthandler;

public class Item {
  private Integer id;
  private String name;

  public String toString(){
    return new StringBuilder()
            .append("Item(")
            .append(id)
            .append(", ")
            .append(name)
            .append(" )")
            .toString();
  }

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
