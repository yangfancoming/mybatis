
package org.apache.ibatis.submitted.cache;

import org.apache.ibatis.cache.impl.PerpetualCache;

import java.util.Date;

public class CustomCache extends PerpetualCache {

  private String stringValue;
  private Integer integerValue;
  private int intValue;
  private Long longWrapperValue;
  private long longValue;
  private Short shortWrapperValue;
  private short shortValue;
  private Float floatWrapperValue;
  private float floatValue;
  private Double doubleWrapperValue;
  private double doubleValue;
  private Byte byteWrapperValue;
  private byte byteValue;
  private Boolean booleanWrapperValue;
  private boolean booleanValue;
  private Date date;

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

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
  }

  public Long getLongWrapperValue() {
    return longWrapperValue;
  }

  public void setLongWrapperValue(Long longWrapperValue) {
    this.longWrapperValue = longWrapperValue;
  }

  public Short getShortWrapperValue() {
    return shortWrapperValue;
  }

  public void setShortWrapperValue(Short shortWrapperValue) {
    this.shortWrapperValue = shortWrapperValue;
  }

  public short getShortValue() {
    return shortValue;
  }

  public void setShortValue(short shortValue) {
    this.shortValue = shortValue;
  }

  public Float getFloatWrapperValue() {
    return floatWrapperValue;
  }

  public void setFloatWrapperValue(Float floatWrapperValue) {
    this.floatWrapperValue = floatWrapperValue;
  }

  public float getFloatValue() {
    return floatValue;
  }

  public void setFloatValue(float floatValue) {
    this.floatValue = floatValue;
  }

  public Double getDoubleWrapperValue() {
    return doubleWrapperValue;
  }

  public void setDoubleWrapperValue(Double doubleWrapperValue) {
    this.doubleWrapperValue = doubleWrapperValue;
  }

  public double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(double doubleValue) {
    this.doubleValue = doubleValue;
  }

  public Byte getByteWrapperValue() {
    return byteWrapperValue;
  }

  public void setByteWrapperValue(Byte byteWrapperValue) {
    this.byteWrapperValue = byteWrapperValue;
  }

  public byte getByteValue() {
    return byteValue;
  }

  public void setByteValue(byte byteValue) {
    this.byteValue = byteValue;
  }

  public Boolean getBooleanWrapperValue() {
    return booleanWrapperValue;
  }

  public void setBooleanWrapperValue(Boolean booleanWrapperValue) {
    this.booleanWrapperValue = booleanWrapperValue;
  }

  public boolean isBooleanValue() {
    return booleanValue;
  }

  public void setBooleanValue(boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

}
