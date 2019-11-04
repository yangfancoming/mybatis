package org.apache.goat.chapter200.D05;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/29---19:31
 */
public class TargetProxy implements InvocationHandler {

  private Object target;

  public TargetProxy() {
  }

  public TargetProxy(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println(" TargetProxy 前。。。");
    Object result = null;
    if (target != null){
      System.out.println("反射调用被代理对象的业务方法 方法名为："+ method.getName());
      result = method.invoke(target, args);
    }else {
      System.out.println("被代理对象为空，不做反射调用！");
    }
    System.out.println(" TargetProxy 后。。。");
    return result;
  }

  public static Object wrap(Object target) {
    InvocationHandler targetProxy = new TargetProxy(target);
    return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),targetProxy);
  }
}
