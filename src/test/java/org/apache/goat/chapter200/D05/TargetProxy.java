package org.apache.goat.chapter200.D05;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: 自定义代理需要实现InvocationHandler接口
 * @ author  山羊来了
 * @ date 2019/10/29---19:31
 */
public class TargetProxy implements InvocationHandler {

  private Object target;

  public TargetProxy(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println(" 前置处理。。。");
    Object result = null;
    if (target != null){
      System.out.println("jdk动态代理调用 方法名为："+ method.getName() + "参数为：" + args);
      result = method.invoke(target, args);
    }else {
      System.out.println("被代理对象为空，不做反射调用！");
    }
    System.out.println(" 后置处理。。。");
    return result;
  }

  public Object wrap() {
    return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
  }
}
