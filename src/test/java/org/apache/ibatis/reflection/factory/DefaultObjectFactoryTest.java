
package org.apache.ibatis.reflection.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;


class DefaultObjectFactoryTest {

  DefaultObjectFactory defaultObjectFactory = new DefaultObjectFactory();

  @Test
  void createClass() {
    List<Class<?>> list = Arrays.asList(String.class, Integer.class);
    List<Object> foo = Arrays.asList("foo", 0); // ok
//        List<Object> foo = Arrays.asList("foo", "bar"); // error
    TestClass testClass = defaultObjectFactory.create(TestClass.class,list,foo);
    Assertions.assertEquals((Integer) 0, testClass.myInteger, "myInteger didn't match expected");
    Assertions.assertEquals("foo", testClass.myString, "myString didn't match expected");
  }

  @Test
  void createClassThrowsProperErrorMsg() {
    // 没有单个 String 的构造函数 ： Caused by: java.lang.NoSuchMethodException: org.apache.ibatis.reflection.factory.TestClass.<init>(java.lang.String)
    List<Class<?>> list = Collections.singletonList(String.class);
    List<Object> foo =  Collections.singletonList("foo");
    TestClass testClass = defaultObjectFactory.create(TestClass.class, list, foo);
    System.out.println(testClass);
  }


}
