
package org.apache.goat.chapter200.common;

import java.io.Serializable;

public class Foo implements Serializable {

  private int id;
  private String firstname;
  private String lastname;

  public Foo() {
  }

  public Foo(int id, String firstname, String lastname) {
    setId(id);
    setFirstname(firstname);
    setLastname(lastname);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("id=" + id);
    sb.append(", lastname=" + lastname);
    sb.append(", firstname=" + firstname);
    return sb.toString();
  }

}
