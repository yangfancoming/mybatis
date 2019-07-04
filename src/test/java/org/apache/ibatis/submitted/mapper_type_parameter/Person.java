
package org.apache.ibatis.submitted.mapper_type_parameter;

public class Person {
  private Integer id;

  private String name;

  public Person() {
    super();
  }

  public Person(Integer id) {
    super();
    this.id = id;
  }

  public Person(String name) {
    super();
    this.name = name;
  }

  public Person(Integer id, String name) {
    super();
    this.id = id;
    this.name = name;
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
