
package org.apache.ibatis.reflection.factory;


public class TestClass {

  public String myString;
  public Integer myInteger;

//  private String myString;
//  private Integer myInteger;


  public TestClass(String myString, Integer myInteger) {
    this.myString = myString;
    this.myInteger = myInteger;
  }

//  public TestClass(String myString) {
//    this.myString = myString;
//  }
}
