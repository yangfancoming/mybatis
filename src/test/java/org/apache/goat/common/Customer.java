package org.apache.goat.common;

import java.util.Date;

/**
 * Created by 64274 on 2019/10/23.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/23---11:35
 */
public class Customer {

  private Integer id;
  private String name;
  private String email;
  private Date birth;

  public Customer() {
  }

  public Customer(Integer id, String name, String email, Date birth) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.birth = birth;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getBirth() {
    return birth;
  }

  public void setBirth(Date birth) {
    this.birth = birth;
  }

  @Override
  public String toString() {
    return "Customer{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", birth=" + birth + '}';
  }
}

