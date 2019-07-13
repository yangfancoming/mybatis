
package org.apache.ibatis.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;


public class MapperProxyFactory<T> {

  //mapperInterface就是Mapper接口  被代理类
  private final Class<T> mapperInterface;

  //支持对被代理类进行缓存  methodCache就是对Mapper接口中的方法和方法的封装类（MapperMethod）的映射。 MapperMethod处理的事情主要就是：处理Mapper接口中方法的注解，参数，和返回值
  private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

  public MapperProxyFactory(Class<T> mapperInterface) {
    this.mapperInterface = mapperInterface;
  }

  public Class<T> getMapperInterface() {
    return mapperInterface;
  }

  public Map<Method, MapperMethod> getMethodCache() {
    return methodCache;
  }

  // 创建 MapperProxy 对象， MapperProxy 实现了 InvocationHandler 接口，代理逻辑封装在此类中
  public T newInstance(SqlSession sqlSession) {
    final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }

  @SuppressWarnings("unchecked")
  protected T newInstance(MapperProxy<T> mapperProxy) {
    // 通过 JDK 动态代理创建代理对象
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }

}
