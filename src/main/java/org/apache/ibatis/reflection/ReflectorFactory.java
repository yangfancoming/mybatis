
package org.apache.ibatis.reflection;


//  ReflectorFactory接口主要实现了对Reflector对象的创建和缓存
public interface ReflectorFactory {

  // 是否会缓存Reflector对象
  boolean isClassCacheEnabled();

  // 设置是否缓存Reflector对象
  void setClassCacheEnabled(boolean classCacheEnabled);

  /**
   * 创建指定Class的Reflector对象
   */
  Reflector findForClass(Class<?> type);
}
