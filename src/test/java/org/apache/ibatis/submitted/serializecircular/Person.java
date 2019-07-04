
package org.apache.ibatis.submitted.serializecircular;

import java.io.Serializable;

public class Person implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer id;
  private Department department;

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

}
