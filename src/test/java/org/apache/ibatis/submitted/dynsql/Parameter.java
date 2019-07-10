
package org.apache.ibatis.submitted.dynsql;

import java.util.List;


public class Parameter {
  private String schema;
  private List<Integer> ids;
  private boolean enabled;

  public String getFred() {
    // added this method to check for bug with DynamicContext
    // IBATIS-777
    throw new RuntimeException("This method should not be called.");
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public List<Integer> getIds() {
    return ids;
  }

  public void setIds(List<Integer> ids) {
    this.ids = ids;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
