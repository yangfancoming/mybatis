
package org.apache.ibatis.binding;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * 映射器代理，代理模式
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

  private static final long serialVersionUID = -6424540398559729838L;
  // 记录了关联的 SqlSession 对象
  private final SqlSession sqlSession;
  // mapper 接口对应的Class对象  eg: interface org.apache.goat.chapter100.C.C012.EmployeeMapper
  private final Class<T> mapperInterface;
  /**
   * 用于缓存 MapperMethod 对象，其中key是Mapper接口中方法对应的Method对象，value是对应的MapperMethod 对象。
   * MapperMethod 对象会完成参数转换以及SQL语句的执行功能
   * 需要注意的是， MapperMethod 中并不记录任何状态相关的信息，所以可以在多个代理对象之间共享
  */
  private final Map<Method, MapperMethod> methodCache;

  public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

  /** 这里会拦截Mapper接口中的所有方法
   * 执行动态代理的所有方法时,都会白替换成执行如下的invoke方法
   * @param proxy  代表 被代理对象
   * @param method 代表 正在执行的方法
   * @param args   代表 调用目标方法时传入的实参
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    /**
     *  代理以后，所有Mapper的方法调用时，都会调用这个invoke方法
     *  1.先判断执行的方法是不是Object类的方法，比如toString()，hashcode() 等方法，是的话则直接反射执行这些方法
     *  2.如果不是，从缓存中获取MapperMethod，如果为空则创建并加入缓存，然后执行sql语句
     *  org.apache.goat.chapter100.E001.EmployeeMapper
     */
    try {
      if (Object.class.equals(method.getDeclaringClass())) { // 如果是Object中的方法 则直接放行 #测试用例 org.apache.goat.chapter700.A.App
        return method.invoke(this, args);
        /*
         * 下面的代码最早出现在 mybatis-3.4.2 版本中，用于支持 JDK 1.8 中的新特性 - 接口默认方法。
         * 去 Github 上看一下相关的相关的讨论（issue #709），链接如下：https://github.com/mybatis/mybatis-3/issues/709
         */
      } else if (method.isDefault()) { // 如果是java8中的接口默认方法 则直接放行 #测试用例 org.apache.goat.chapter700.A.App
        return invokeDefaultMethod(proxy, method, args);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
    /** 其他Mapper接口定义的方法交由 mapperMethod 来执行
     * 最后2句关键，我们执行所调用Mapper的每一个接口方法，最后返回的是MapperMethod.execute方法。
     * 每一个MapperMethod对应了一个mapper文件中配置的一个sql语句或FLUSH配置，对应的sql语句通过mapper对应的class文件名+方法名从Configuration对象中获得。
     * 从缓存中获取 MapperMethod 对象，若缓存未命中，则创建 MapperMethod 对象
    */
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    return mapperMethod.execute(sqlSession, args);  // 调用 execute 方法执行 SQL
  }

  /**
   *    private final Map<Method, MapperMethod> methodCache;
   *    Mapper接口中的每个方法都会生成一个MapperMethod对象, methodCache维护着他们的对应关系
   *    获取方法对象来获取接口方法mapperMethod
   *
   *    老版本代码
   *   private MapperMethod cachedMapperMethod(Method method) {
   *     MapperMethod mapperMethod = methodCache.get(method);
   *     if (mapperMethod == null) {
   *       mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
   *       methodCache.put(method, mapperMethod);
   *     }
   *     return mapperMethod;
   *   }
   *   若key对应的value为空，会将第二个参数的返回值存入并返回
  */
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
