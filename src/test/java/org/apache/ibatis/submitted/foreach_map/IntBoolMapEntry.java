
package org.apache.ibatis.submitted.foreach_map;

public class IntBoolMapEntry {
  public IntBoolMapEntry() {
  }

  public IntBoolMapEntry(Integer key, Boolean value) {
    this.key = key;
    this.value = value;
  }

  public Integer getKey() {
    return key;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    IntBoolMapEntry mapEntry = (IntBoolMapEntry) o;

    if (key != null ? !key.equals(mapEntry.key) : mapEntry.key != null)
      return false;
    if (value != null ? !value.equals(mapEntry.value) : mapEntry.value != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = key != null ? key.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return '{' + key.toString() + '=' + value + '}';
  }

  private Integer key;
  private Boolean value;
}
