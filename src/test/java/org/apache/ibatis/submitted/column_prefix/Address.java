
package org.apache.ibatis.submitted.column_prefix;

public class Address {
  private Integer id;

  private String state;

  private String city;

  private Phone phone1;

  private Phone phone2;

  private boolean hasPhone;

  private String stateBird;

  private Zip zip;

  public Address(Integer id, String state) {
    super();
    this.id = id;
    this.state = state;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Phone getPhone1() {
    return phone1;
  }

  public void setPhone1(Phone phone1) {
    this.phone1 = phone1;
  }

  public Phone getPhone2() {
    return phone2;
  }

  public void setPhone2(Phone phone2) {
    this.phone2 = phone2;
  }

  public boolean isHasPhone() {
    return hasPhone;
  }

  public void setHasPhone(boolean hasPhone) {
    this.hasPhone = hasPhone;
  }

  public String getStateBird() {
    return stateBird;
  }

  public void setStateBird(String stateBird) {
    this.stateBird = stateBird;
  }

  public Zip getZip() {
    return zip;
  }

  public void setZip(Zip zip) {
    this.zip = zip;
  }
}
