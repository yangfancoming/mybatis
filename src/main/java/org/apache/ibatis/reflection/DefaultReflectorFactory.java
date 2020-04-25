
package org.apache.ibatis.reflection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// DefaultReflectorFactory 用于创建 Reflector，同时兼有缓存的功能
public class DefaultReflectorFactory implements ReflectorFactory {

  /*** 默认开启对Reflector对象的缓存 */
  /** 反射器是否需要缓存，默认需要*/
  private boolean classCacheEnabled = true;

  /** 目标类和反射器映射缓存  使用集合ConcurrentHashMap实现对Reflector的缓存*/
  /** 缓存了多个类Class的反射器Reflector。（避免一个类，多次重复反射） */
  /**类与反射器对象映射*/
  private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

  public DefaultReflectorFactory() { }

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
    // classCacheEnabled 默认为 true  //检查是否开启缓存
    if (classCacheEnabled) {
      // synchronized (type) removed see issue #461 doit  这里的 clazz参数是怎么传入的？？  public Reflector(Class<?> clazz) {
      // 类反射器是否存在，不存在实例化，存在则返回
      return reflectorMap.computeIfAbsent(type, Reflector::new);
    } else {
      // 没有开启缓存,直接创建Reflector对象并返回
      return new Reflector(type);
    }
  }

}
