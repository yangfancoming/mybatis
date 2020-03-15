package org.apache.goat.chapter100.A.A025.item01;

/**
 * Created by Administrator on 2020/3/15.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/15---13:03
 */
public enum SexEnum {

  male(0, "男"), female(1, "女");

  private int id;
  private String name;

  SexEnum(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
