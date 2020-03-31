
package org.apache.ibatis.io;

import static org.junit.jupiter.api.Assertions.*;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;

import org.apache.ibatis.annotations.CacheNamespace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class ResolverUtilTest {

  private static ClassLoader currentContextClassLoader;

  @BeforeAll
  static void setUp() {
    currentContextClassLoader = Thread.currentThread().getContextClassLoader();
  }

  // 测试 getClasses 方法
  @Test
  void getClasses() {
    assertEquals(new ResolverUtil<>().getClasses().size(), 0);
  }

  //  测试 getClassLoader 方法
  @Test
  void getClassLoader() {
    assertEquals(new ResolverUtil<>().getClassLoader(), currentContextClassLoader);
  }

  ResolverUtil resolverUtilTemp = new ResolverUtil();

  //  测试 setClassLoader 方法
  @Test
  void setClassLoader() {
    AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
      resolverUtilTemp.setClassLoader(new ClassLoader() {});
      return null;
    });
    assertNotEquals(resolverUtilTemp.getClassLoader(), currentContextClassLoader);
  }

  //  测试 getPackagePath 方法
  @Test
  void getPackagePath() {
    assertNull(resolverUtilTemp.getPackagePath(null));
    assertEquals(resolverUtilTemp.getPackagePath("org.apache.ibatis.io"), "org/apache/ibatis/io");
  }

  ResolverUtil.Test test = new ResolverUtil.IsA(VFS.class);

  ResolverUtil<VFS> resolverUtil = new ResolverUtil<>();

  //  测试 addIfMatching 方法  匹配的【成功】的情况
  @Test
  void addIfMatching() {
    resolverUtil.addIfMatching(test, "org/apache/ibatis/io/DefaultVFS.class"); // true
    resolverUtil.addIfMatching(test, "org/apache/ibatis/io/JBoss6VFS.class"); // true
    resolverUtil.addIfMatching(test, "org/apache/ibatis/io/VFS.class"); // true
    Set<Class<? extends VFS>> classSets = resolverUtil.getClasses();
    assertEquals(classSets.size(), 3);
    classSets.forEach(c -> assertTrue(VFS.class.isAssignableFrom(c)));
  }

  //  测试 addIfMatching 方法  匹配的【失败】的情况
  @Test
  void addIfNotMatching() {
    resolverUtil.addIfMatching(test, "org/apache/ibatis/io/Resources.class");
    assertEquals(resolverUtil.getClasses().size(), 0);
  }

  //  测试 find 方法
  // doit 这里个方法里 会创建几个ResolverUtil的实例？？？
  @Test
  void find() {
    resolverUtil.find(test, "org.apache.ibatis.io");
    Set<Class<? extends VFS>> classSets = resolverUtil.getClasses();
    //org.apache.ibatis.io.VFS
    //org.apache.ibatis.io.DefaultVFS
    //org.apache.ibatis.io.JBoss6VFS
    assertEquals(classSets.size(), 3);
    classSets.forEach(c -> assertTrue(VFS.class.isAssignableFrom(c)));
  }

  // 测试 findImplementations 查找实现类   packageNames 为空的情况
  @Test
  void findImplementationsWithNullPackageName() {
    resolverUtil.findImplementations(VFS.class, null);
    assertEquals(resolverUtil.getClasses().size(), 0);
  }

  // 测试 findImplementations 查找实现类 正常packageNames的情况
  @Test
  void findImplementations() {
    resolverUtil.findImplementations(VFS.class, "org.apache.ibatis.io");
    Set<Class<? extends VFS>> classSets = resolverUtil.getClasses();
    assertEquals(classSets.size(), 3);
    classSets.forEach(c -> assertTrue(VFS.class.isAssignableFrom(c)));
    classSets.forEach(c ->System.out.println(c.getCanonicalName()));
  }

  ResolverUtil<Object> resolverUtilObject = new ResolverUtil<>();

  @Test
  void findAnnotatedWithNullPackageName() {
    resolverUtilObject.findAnnotated(CacheNamespace.class, null);
    assertEquals(resolverUtilObject.getClasses().size(), 0);
  }

  // 测试 findAnnotated 方法
  @Test
  void findAnnotated() {
    String name = this.getClass().getPackage().getName();// org.apache.ibatis.io 包括源码目录和测试目录 2个目录的class文件
    resolverUtilObject.findAnnotated(CacheNamespace.class,name );
    Set<Class<?>> classSets = resolverUtilObject.getClasses();
    //org.apache.ibatis.io.ResolverUtilTest.TestMapper
    assertEquals(classSets.size(), 1);
    classSets.forEach(c -> assertNotNull(c.getAnnotation(CacheNamespace.class)));
  }

  @Test
  void testToString() {
    System.out.println(VFS.class.getSimpleName());
    System.out.println(test.toString());
    ResolverUtil.AnnotatedWith annotatedWith = new ResolverUtil.AnnotatedWith(CacheNamespace.class);
    System.out.println(annotatedWith.toString());
    System.out.println("@" + CacheNamespace.class.getSimpleName());
  }

  @CacheNamespace(readWrite = false)
  private interface TestMapper {
    //test ResolverUtil.findAnnotated method
  }

}
