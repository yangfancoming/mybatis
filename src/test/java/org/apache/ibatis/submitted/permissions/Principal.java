
package org.apache.ibatis.submitted.permissions;

import java.util.ArrayList;
import java.util.List;

public class Principal {

  private String principalName;
  private List<Permission> permissions = new ArrayList<>();

  public String getPrincipalName() {
    return principalName;
  }

  public void setPrincipalName(String principalName) {
    this.principalName = principalName;
  }

  public List<Permission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }

}
