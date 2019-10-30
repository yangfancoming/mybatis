package org.apache.goat.chapter200.D07;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: Invocation 类就是被代理对象的封装，也就是要拦截的真正对象
 * @ author  山羊来了
 * @ date 2019/10/29---21:41
 */
public class TargetProxy implements InvocationHandler {

  private Object target;

  private Interceptor interceptor;

  public TargetProxy(Object target,Interceptor interceptor) {
    this.target = target;
    this.interceptor = interceptor;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Invocation invocation = new Invocation(target,method,args);
    return interceptor.intercept(invocation);
  }

  public static Object wrap(Object target,Interceptor interceptor) {
    TargetProxy targetProxy = new TargetProxy(target, interceptor);
    return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),targetProxy);
  }
}
