
package org.apache.ibatis.submitted.resultmapwithassociationstest;

import java.util.List;

public class Person {
  private int id;
  private List<Address> addresses;

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(final List<Address> addresses) {
    this.addresses = addresses;
  }
}
