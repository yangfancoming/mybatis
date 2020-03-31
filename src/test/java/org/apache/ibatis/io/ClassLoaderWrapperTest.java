
package org.apache.ibatis.io;

import org.apache.ibatis.BaseDataTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ClassLoaderWrapperTest extends BaseDataTest {

  private ClassLoaderWrapper wrapper = new ClassLoaderWrapper();
  private ClassLoader loader = getClass().getClassLoader();
  private final String RESOURCE_NOT_FOUND = "some_resource_that_does_not_exist.properties";
  private final String CLASS_NOT_FOUND = "some.random.class.that.does.not.Exist";
  private final String CLASS_FOUND = "java.lang.Object";

  // 测试通过 类全限定名反射该类
  @Test
  void classForName() throws ClassNotFoundException {
    Class<?> aClass = wrapper.classForName(CLASS_FOUND);
    assertTrue(aClass instanceof Object);
  }

  // 测试通过 类全限定名反射 任意类
  @Test
  void classForName2() throws Exception {
    Class<?> aClass = wrapper.classForName("org.apache.ibatis.io.ResolverUtil");
    assertEquals("org.apache.ibatis.io.ResolverUtil",aClass.getCanonicalName());
    ResolverUtil o = (ResolverUtil)aClass.newInstance();
    System.out.println(o.getClassLoader());
  }

  // 测试 找不到类名的异常
  @Test
  void classForNameNotFound() {
    Assertions.assertThrows(ClassNotFoundException.class, () -> assertNotNull(wrapper.classForName(CLASS_NOT_FOUND)));
  }

  // 测试 使用类加载器 反射该类
  @Test
  void classForNameWithClassLoader() throws ClassNotFoundException {
    Class<?> aClass = wrapper.classForName(CLASS_FOUND, loader);
    assertTrue(aClass instanceof Object);
  }

  // 测试 通过 url字符串 获取 URL类
  @Test
  void getResourceAsURL() {
    URL url = wrapper.getResourceAsURL(JPETSTORE_PROPERTIES);
    System.out.println(url);
  }

  // 测试 通过 url字符串 + 类加载器   获取 URL类
  @Test
  void getResourceAsURLWithClassLoader() {
    URL url = wrapper.getResourceAsURL(JPETSTORE_PROPERTIES, loader);
    System.out.println(url);
  }

  @Test
  void getResourceAsURLNotFound() {
    assertNull(wrapper.getResourceAsURL(RESOURCE_NOT_FOUND));
  }

  @Test
  void getResourceAsStream() {
    assertNotNull(wrapper.getResourceAsStream(JPETSTORE_PROPERTIES));
  }

  @Test
  void getResourceAsStreamNotFound() {
    assertNull(wrapper.getResourceAsStream(RESOURCE_NOT_FOUND));
  }

  @Test
  void getResourceAsStreamWithClassLoader() {
    assertNotNull(wrapper.getResourceAsStream(JPETSTORE_PROPERTIES, loader));
  }

}
