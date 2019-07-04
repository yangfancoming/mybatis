
package org.apache.ibatis.submitted.nestedresulthandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Person {
  private Integer id;
  private String name;
  private List<Item> items=new ArrayList<>();

  public String toString(){
    return new StringBuilder()
            .append("Person(")
            .append(id)
            .append(", ")
            .append(name)
            .append(", ")
            .append(items)
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

  public Collection<Item> getItems() {
    return items;
  }

  public boolean owns(String name) {
    for (Item item : getItems()) {
      if (item.getName().equals(name))
        return true;
    }
    return false;
  }
}
