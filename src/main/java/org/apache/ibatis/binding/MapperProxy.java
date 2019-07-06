
package org.apache.ibatis.binding;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;


public class MapperProxy<T> implements InvocationHandler, Serializable {

  private static final long serialVersionUID = -6424540398559729838L;
  private final SqlSession sqlSession;
  private final Class<T> mapperInterface;
  private final Map<Method, MapperMethod> methodCache;

  public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      // 如果方法是定义在 Object 类中的，则直接调用
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, args);
        /*
         * 下面的代码最早出现在 mybatis-3.4.2 版本中，用于支持 JDK 1.8 中的
         * 新特性 - 默认方法。这段代码的逻辑就不分析了， 有兴趣的同学可以
         * 去 Github 上看一下相关的相关的讨论（issue #709），链接如下：
         * https://github.com/mybatis/mybatis-3/issues/709
         */
      } else if (method.isDefault()) {
        return invokeDefaultMethod(proxy, method, args);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
    // 从缓存中获取 MapperMethod 对象，若缓存未命中，则创建 MapperMethod 对象
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    // 调用 execute 方法执行 SQL
    return mapperMethod.execute(sqlSession, args);
  }

  private MapperMethod cachedMapperMethod(Method method) {
    return methodCache.computeIfAbsent(method, k -> new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()));
  }

  private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
    final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
    if (!constructor.isAccessible()) {
      constructor.setAccessible(true);
    }
    final Class<?> declaringClass = method.getDeclaringClass();
    return constructor
      .newInstance(declaringClass, MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED  | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
      .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
  }
}
