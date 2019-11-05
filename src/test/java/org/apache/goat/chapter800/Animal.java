package org.apache.goat.chapter800;

/**
 * Created by 64274 on 2019/11/5.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/5---10:07
 */
public class Animal {
  private Animal parent;
  private String name;

  public Animal getParent() {
    return parent;
  }

  public void setParent(Animal parent) {
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
