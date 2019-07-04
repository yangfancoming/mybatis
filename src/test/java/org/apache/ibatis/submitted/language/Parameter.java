
package org.apache.ibatis.submitted.language;

public class Parameter {

  private final boolean includeLastName;

  private final String name;

  public Parameter(boolean includeLastName, String name) {
    this.includeLastName = includeLastName;
    this.name = name;
  }

  public boolean isIncludeLastName() {
    return includeLastName;
  }

  public String getName() {
    return name;
  }

}
