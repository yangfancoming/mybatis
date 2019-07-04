
package org.apache.ibatis.submitted.lazy_properties;

import java.util.List;

public class User implements Cloneable {
  private Integer id;
  private String name;
  private User lazy1;
  private User lazy2;
  private List<User> lazy3;
  public int setterCounter;

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

  public User getLazy1() {
    return lazy1;
  }

  public void setLazy1(User lazy1) {
    setterCounter++;
    this.lazy1 = lazy1;
  }

  public User getLazy2() {
    return lazy2;
  }

  public void setLazy2(User lazy2) {
    setterCounter++;
    this.lazy2 = lazy2;
  }

  public List<User> getLazy3() {
    return lazy3;
  }

  public void setLazy3(List<User> lazy3) {
    setterCounter++;
    this.lazy3 = lazy3;
  }

  public void trigger() {
    // nop
  }

  @Override
  public Object clone() {
    return new User();
  }
}
