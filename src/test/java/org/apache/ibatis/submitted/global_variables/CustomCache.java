
package org.apache.ibatis.submitted.global_variables;

import org.apache.ibatis.cache.impl.PerpetualCache;

public class CustomCache extends PerpetualCache {

  private String stringValue;
  private Integer integerValue;
  private long longValue;

  public CustomCache(String id) {
    super(id);
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public Integer getIntegerValue() {
    return integerValue;
  }

  public void setIntegerValue(Integer integerValue) {
    this.integerValue = integerValue;
  }

  public long getLongValue() {
    return longValue;
  }

  public void setLongValue(long longValue) {
    this.longValue = longValue;
  }

}
