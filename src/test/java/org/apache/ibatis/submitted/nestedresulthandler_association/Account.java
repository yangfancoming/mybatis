
package org.apache.ibatis.submitted.nestedresulthandler_association;

import java.util.Date;

public class Account {
  private String accountUuid;

  private String accountName;

  private Date birthDate;

  private AccountAddress address;

  public String getAccountUuid() {
    return accountUuid;
  }

  public void setAccountUuid(String accountUuid) {
    this.accountUuid = accountUuid;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public AccountAddress getAddress() {
    return address;
  }

  public void setAddress(AccountAddress address) {
    this.address = address;
  }
}
