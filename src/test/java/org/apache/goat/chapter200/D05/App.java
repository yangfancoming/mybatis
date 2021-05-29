package org.apache.goat.chapter200.D05;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 嗯，这思路是正确的了。
 * 但还是存在问题，execute() 是业务代码，我把所有的要拦截处理的逻辑都写到TargetProxy类的invoke方法里面了，
 * TargetProxy 是动态代理类，也可以理解成是个工具类，我们不应该把业务代码写到TargetProxy类里。
 * 不符合面向对象的思想，可以抽象一下处理。可以设计一个Interceptor接口，需要做什么业务拦截处理实现该接口就行了。 详见D06
 */
public class App {

  Target target = new TargetImpl();

  // newProxyInstance 参数为接口
  @Test
  public void test1(){
    InvocationHandler targetProxy = new TargetProxy(target);
    // hook 是被钩对象
    Target hook = (Target) Proxy.newProxyInstance(Target.class.getClassLoader(), new Class[]{Target.class}, targetProxy);
    // 被钩对象执行时 先去执行钩子函数逻辑（TargetProxy#invoke）  再去执行目标函数逻辑（TargetImpl#execute）
    System.out.println("返回值为：" + hook.execute("goat"));
  }

  // newProxyInstance 参数为实现类
  @Test
  public void test2(){
    InvocationHandler targetProxy = new TargetProxy(target);
    Target hook = (Target) Proxy.newProxyInstance(TargetImpl.class.getClassLoader(), TargetImpl.class.getInterfaces(), targetProxy);
    System.out.println("返回值为：" + hook.execute("goat"));
  }

  @Test
  public void test4(){
    // 实际调用方法的时候，是调用TargetProxy的invoke()方法
    TargetProxy targetProxy = new TargetProxy(target);
    Target hook = (Target) targetProxy.wrap();
    System.out.println("返回值为：" + hook.execute(" HelloWord "));
  }

  @Test
  public void test5(){
    TargetProxy targetProxy = new TargetProxy(target);
    Target hook = (Target) targetProxy.wrap();
    System.out.println("返回值为：" + hook.test(" HelloWord "));
  }
}
