
package org.apache.ibatis.reflection;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// DefaultReflectorFactory 用于创建 Reflector，同时兼有缓存的功能
public class DefaultReflectorFactory implements ReflectorFactory {

  private static final Log log = LogFactory.getLog(DefaultReflectorFactory.class);

  /***  对Reflector对象的缓存 （默认开启） */
  private boolean classCacheEnabled = true;

  /**
   * 类与反射器对象映射
   * 目标类和反射器映射缓存  使用集合ConcurrentHashMap实现对Reflector的缓存
   * 缓存了多个类Class的反射器Reflector。（避免一个类，多次重复反射）
  */
  private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

  public DefaultReflectorFactory() { // -modify
    log.warn("进入 【DefaultReflectorFactory】 无参 构造函数 {}");
  }

  @Override
  public boolean isClassCacheEnabled() {
    return classCacheEnabled;
  }

  @Override
  public void setClassCacheEnabled(boolean classCacheEnabled) {
    this.classCacheEnabled = classCacheEnabled;
  }

  // 为指定的Class创建Reflector对象，并将Reflector对象缓存到reflectorMap中
  @Override
  public Reflector findForClass(Class<?> type) {
    if (classCacheEnabled) {
      /**
       * synchronized (type) removed see issue #461
       *  类反射器是否存在，不存在实例化，存在则返回
       * System.out::println 可以看作 lambda表达式 e -> System.out.println(e) 的缩写形式。
       * 因此下面两种写法是完全等价的：
       * return reflectorMap.computeIfAbsent(type, Reflector::new);
       * return reflectorMap.computeIfAbsent(type, e -> new Reflector(e));
       * 其中的e变量是computeIfAbsent函数自动推导出来的
      */
      return reflectorMap.computeIfAbsent(type,Reflector::new);
    } else {
      // 没有开启缓存,直接创建Reflector对象并返回
      return new Reflector(type);
    }
  }

}
