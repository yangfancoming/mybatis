
package org.apache.ibatis.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



public class DefaultReflectorFactory implements ReflectorFactory {

  /*** 默认开启对Reflector对象的缓存 */
  private boolean classCacheEnabled = true;

  /** 目标类和反射器映射缓存  使用集合ConcurrentHashMap实现对Reflector的缓存*/
  /** 缓存了多个类Class的反射器Reflector。（避免一个类，多次重复反射） */
  private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

  public DefaultReflectorFactory() {
  }

  @Override
  public boolean isClassCacheEnabled() {
    return classCacheEnabled;
  }

  @Override
  public void setClassCacheEnabled(boolean classCacheEnabled) {
    this.classCacheEnabled = classCacheEnabled;
  }

  /**
   * 为指定的Class创建Reflector对象，并将Reflector对象缓存到reflectorMap中
   */
  @Override
  public Reflector findForClass(Class<?> type) {
    // classCacheEnabled 默认为 true  //检查是否开启缓存
    if (classCacheEnabled) {
      // synchronized (type) removed see issue #461
      return reflectorMap.computeIfAbsent(type, Reflector::new);
    } else {
      //没有开启缓存,直接创建Reflector对象并返回
      return new Reflector(type);
    }
  }

}
