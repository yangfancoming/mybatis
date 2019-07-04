
package org.apache.ibatis.submitted.call_setters_on_nulls_again;

import java.util.List;

public class ChildBean {

  private String name;

  private ChildBean child;

  private List<ChildBean> beans;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChildBean getChild() {
    return child;
  }

  public void setChild(ChildBean child) {
    this.child = child;
  }

  @Override
  public String toString() {
    return "ChildBean [name=" + name + ", child=" + child + ", beans=" + beans + "]";
  }

  public List<ChildBean> getBeans() {
    return beans;
  }

  public void setBeans(List<ChildBean> beans) {
    this.beans = beans;
  }
}
