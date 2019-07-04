
package org.apache.ibatis.submitted.foreach_map;

public class StringStringMapEntry {
  public StringStringMapEntry() {
  }

  public StringStringMapEntry(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public Object getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    StringStringMapEntry mapEntry = (StringStringMapEntry) o;

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

  private String key;
  private String value;
}
