
package org.apache.ibatis.submitted.serializecircular;

import java.io.Serializable;

public class Department implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer id;
  private Attribute attribute;
  private Person person;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }

}
