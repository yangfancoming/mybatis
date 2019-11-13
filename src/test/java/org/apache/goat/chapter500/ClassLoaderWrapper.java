
package org.apache.goat.chapter500;

import java.io.InputStream;

/**
 * A class to wrap access to multiple class loaders making them work as one
 */
public class ClassLoaderWrapper {

  ClassLoader defaultClassLoader;
  ClassLoader systemClassLoader;

  ClassLoaderWrapper() {
    try {
      systemClassLoader = ClassLoader.getSystemClassLoader();
    } catch (SecurityException ignored) {
    }
  }

  /**
   * Get a resource from the classpath
   * @param resource - the resource to find
   * @return the stream or null
   */
  public InputStream getResourceAsStream(String resource) {
    return getResourceAsStream(resource, getClassLoaders(null));
  }

  /**
   * Get a resource from the classpath, starting with a specific class loader
   * @param resource    - the resource to find
   * @param classLoader - the first class loader to try
   * @return the stream or null
   */
  public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
    return getResourceAsStream(resource, getClassLoaders(classLoader));
  }

  /**
   * Try to get a resource from a group of classloaders
   * @param resource    - the resource to get
   * @param classLoader - the classloaders to examine
   * @return the resource or null
   */
  InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
    //循环ClassLoader 数组
    for (ClassLoader cl : classLoader) {
      if (null != cl) {
        // try to find the resource as passed  // 根据入参resource，读取该路径下文件
        InputStream returnValue = cl.getResourceAsStream(resource);
        // now, some class loaders want this leading "/", so we'll add it and try again if we didn't find the resource
        // 如果没有，加上“/”再尝试读取
        if (null == returnValue) {
          returnValue = cl.getResourceAsStream("/" + resource);
        }
        //如果读取到，终止循环，返回结果
        if (null != returnValue) {
          return returnValue;
        }
      }
    }
    return null;
  }


  ClassLoader[] getClassLoaders(ClassLoader classLoader) {
    return new ClassLoader[]{
      classLoader,  //参数指定的类加载器
      defaultClassLoader, // 系统指定的默认加载器
      Thread.currentThread().getContextClassLoader(),//当前线程绑定的类加载器
      getClass().getClassLoader(), // 当前类使用的类加载器
      systemClassLoader // System ClassLoader(App ClassLoader)
    };
  }

}
