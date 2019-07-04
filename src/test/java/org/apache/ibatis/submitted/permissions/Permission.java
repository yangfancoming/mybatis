
package org.apache.ibatis.submitted.permissions;

public class Permission {

  private String permission;
  private Resource resource;

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

}
