
package org.apache.ibatis.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <p>ResolverUtil is used to locate classes that are available in the/a class path and meet
 * arbitrary conditions. The two most common conditions are that a class implements/extends
 * another class, or that is it annotated with a specific annotation. However, through the use
 * of the {@link Test} class it is possible to search using arbitrary conditions.</p>
 *
 * <p>A ClassLoader is used to locate all locations (directories and jar files) in the class
 * path that contain classes within certain packages, and then to load those classes and
 * check them. By default the ClassLoader returned by
 * {@code Thread.currentThread().getContextClassLoader()} is used, but this can be overridden
 * by calling {@link #setClassLoader(ClassLoader)} prior to invoking any of the {@code find()}
 * methods.</p>
 *
 * <p>General searches are initiated by calling the
 * {@link #find(org.apache.ibatis.io.ResolverUtil.Test, String)} ()} method and supplying
 * a package name and a Test instance. This will cause the named package <b>and all sub-packages</b>
 * to be scanned for classes that meet the test. There are also utility methods for the common
 * use cases of scanning multiple packages for extensions of particular classes, or classes
 * annotated with a specific annotation.</p>
 *
 * <p>The standard usage pattern for the ResolverUtil class is as follows:</p>
 *
 * ResolverUtil&lt;ActionBean&gt; resolver = new ResolverUtil&lt;ActionBean&gt;();
 * resolver.findImplementation(ActionBean.class, pkg1, pkg2);
 * resolver.find(new CustomTest(), pkg1);
 * resolver.find(new CustomTest(), pkg2);
 * Collection&lt;ActionBean&gt; beans = resolver.getClasses();

 其中有一个接口、两个内部类，Class对象和Annotation对象被封装成了Test对象
 其核心功能是匹配Class类型
 核心方法：
 public ResolverUtil<T> find(Test test, String packageName)
 public Set<Class<? extends T>> getClasses()

 ResoulverUtil 主要用于解析给定package目录下满足特定条件的class，从源码中可以看到，实际是调用VFS.getInstance().list(path) 解析
 */
public class ResolverUtil<T> {
  /*
   * An instance of Log to use for logging in this class.
   */
  private static final Log log = LogFactory.getLog(ResolverUtil.class);

  /**
   * A simple interface that specifies how to test classes to determine if they
   * are to be included in the results produced by the ResolverUtil.
   */
  public interface Test {
    /**
     * Will be called repeatedly with candidate classes. Must return True if a class is to be included in the results, false otherwise.
     * 将与候选类一起重复调用。如果要在结果中包含类，则必须返回True，否则返回false。
     */
    boolean matches(Class<?> type);
  }

  /**
   * A Test that checks to see if each class is assignable to the provided class. Note that this test will match the parent type itself if it is presented for matching.
   * 检查每个类是否可分配给所提供类的测试。请注意，如果父类型是为匹配而呈现的，则此测试将匹配父类型本身。
   */
  public static class IsA implements Test {
    private Class<?> parent;

    /** Constructs an IsA test using the supplied Class as the parent class/interface. */
    public IsA(Class<?> parentType) {
      this.parent = parentType;
    }

    /** Returns true if type is assignable to the parent type supplied in the constructor. */
    @Override
    public boolean matches(Class<?> type) {
      return type != null && parent.isAssignableFrom(type);
    }

    @Override
    public String toString() {
      return "is assignable to " + parent.getSimpleName();
    }
  }

  /**
   * A Test that checks to see if each class is annotated with a specific annotation. If it is, then the test returns true, otherwise false.
   */
  public static class AnnotatedWith implements Test {

    private Class<? extends Annotation> annotation;

    /** Constructs an AnnotatedWith test for the specified annotation type. */
    public AnnotatedWith(Class<? extends Annotation> annotation) {
      this.annotation = annotation;
    }

    /** Returns true if the type is annotated with the class provided to the constructor. */
    @Override
    public boolean matches(Class<?> type) {
      return type != null && type.isAnnotationPresent(annotation);
    }

    @Override
    public String toString() {
      return "annotated with @" + annotation.getSimpleName();
    }
  }

  /** The set of matches being accumulated. */
  private Set<Class<? extends T>> matches = new HashSet<>();

  /**
   * The ClassLoader to use when looking for classes. If null then the ClassLoader returned
   * by Thread.currentThread().getContextClassLoader() will be used.
   */
  private ClassLoader classloader;

  /**
   * Provides access to the classes discovered so far. If no calls have been made to any of the {@code find()} methods, this set will be empty.
   * @return the set of classes that have been discovered.
   */
  public Set<Class<? extends T>> getClasses() {
    return matches;
  }

  /**
   * Returns the classloader that will be used for scanning for classes. If no explicit
   * ClassLoader has been set by the calling, the context class loader will be used.
   * @return the ClassLoader that will be used to scan for classes
   */
  public ClassLoader getClassLoader() {
    return classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
  }

  /**
   * Sets an explicit ClassLoader that should be used when scanning for classes. If none
   * is set then the context classloader will be used.
   * @param classloader a ClassLoader to use when scanning for classes
   */
  public void setClassLoader(ClassLoader classloader) {
    this.classloader = classloader;
  }

  /**
   * Attempts to discover classes that are assignable to the type provided.
   * In the case that an interface is provided this method will collect implementations.
   *  In the case of a non-interface class, subclasses will be collected.  Accumulated classes can be accessed by calling {@link #getClasses()}.
   * @param parent the class of interface to find subclasses or implementations of
   * @param packageNames one or more package names to scan (including subpackages) for classes
   */
  public ResolverUtil<T> findImplementations(Class<?> parent, String... packageNames) {
    if (packageNames == null) {
      return this;
    }
    Test test = new IsA(parent);
    for (String pkg : packageNames) {
      find(test, pkg);
    }
    return this;
  }

  /**
   * Attempts to discover classes that are annotated with the annotation. Accumulated
   * classes can be accessed by calling {@link #getClasses()}.
   * @param annotation the annotation that should be present on matching classes
   * @param packageNames one or more package names to scan (including subpackages) for classes
   */
  public ResolverUtil<T> findAnnotated(Class<? extends Annotation> annotation, String... packageNames) {
    if (packageNames == null) {
      return this;
    }
    Test test = new AnnotatedWith(annotation);
    for (String pkg : packageNames) {
      find(test, pkg);
    }
    return this;
  }

  /**
   * Scans for classes starting at the package provided and descending into subpackages.
   * 从提供的package开始扫描classes，并且递归扫描所有子包
   * Each class is offered up to the Test as it is discovered, and if the Test returns true the class is retained.
   * 每一个class被发现时都会提供一个Test(验证器)，如果验证返回true，这个class会被保存起来。
   * Accumulated classes can be fetched by calling {@link #getClasses()}.
   * 可以通过调用getClasses() 获取累积类
   * 输入示例：   org.apache.goat.common
   * 输出结果：   org/apache/goat/common
   * @param test an instance of {@link Test} that will be used to filter classes
   * @param packageName the name of the package from which to start scanning for classes, e.g. {@code net.sourceforge.stripes}  eg: org.apache.goat.common
   */
  public ResolverUtil<T> find(Test test, String packageName) {
    String path = getPackagePath(packageName);
    try {
      /**
       * 获取目录下的所有编译后文件
       * org/apache/goat/common/Bar.class
       * org/apache/goat/common/Foo.class
       * org/apache/goat/common/Zoo.class
       * org/apache/goat/common/CreateDB.sql
      */
      List<String> children = VFS.getInstance().list(path);
      for (String child : children) {
        // 忽略掉非class文件
        if (!child.endsWith(".class")) continue; //  -modify
        addIfMatching(test, child);
      }
    } catch (IOException ioe) {
      log.error("Could not read package: " + packageName, ioe);
    }
    return this;
  }

  /**
   * Converts a Java package name to a path that can be looked up with a call to {@link ClassLoader#getResources(String)}.
   * 将Java包名转换为可通过调用ClassLoader类的getResources方法查找的路径
   * 输入示例：   org.apache.goat.common
   * 输出结果：   org/apache/goat/common
   * @param packageName The Java package name to convert to a path
   */
  protected String getPackagePath(String packageName) {
    return packageName == null ? null : packageName.replace('.', '/');
  }

  /**
   * Add the class designated by the fully qualified class name provided to the set of resolved classes if and only if it is approved by the Test supplied.
   * 如果且仅当通过所提供的测试批准时，才将由提供的完全限定类名指定的类添加到解析类集。
   * @param test the test used to determine if the class matches
   * @param fqn the fully qualified name of a class  eg: org/apache/goat/common/Customer.class
   */
  @SuppressWarnings("unchecked")
  protected void addIfMatching(Test test, String fqn) {
    try {
      // org/apache/goat/common/Customer.class  ===>  org.apache.goat.common.Customer
      String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
      ClassLoader loader = getClassLoader();
      if (log.isDebugEnabled()) {
        log.debug("Checking to see if class " + externalName + " matches criteria [" + test + "]");
      }
      Class<?> type = loader.loadClass(externalName);
      if (test.matches(type)) {
        matches.add((Class<T>) type);
      }else {
        log.debug(externalName + " match failed！ modify-");
      }
    } catch (Throwable t) {
      log.warn("Could not examine class '" + fqn + "'" + " due to a " + t.getClass().getName() + " with message: " + t.getMessage());
    }
  }
}
