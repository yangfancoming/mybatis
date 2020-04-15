package org.apache.goat.chapter200.D05;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 嗯，这思路是正确的了。
 * 但还是存在问题，execute() 是业务代码，我把所有的要拦截处理的逻辑都写到invoke方法里面了，
 * 不符合面向对象的思想，可以抽象一下处理。可以设计一个Interceptor接口，
 * 需要做什么拦截处理实现接口就行了。 详见D06
 */
public class App {

  Target target = new TargetImpl();

  // 无参构造函数 导致 没有对象被代理
  @Test
  public void test1(){
    InvocationHandler targetProxy = new TargetProxy();
    common(targetProxy);
  }

  // 有参构造函数 有对象被代理
  @Test
  public void test2(){
    InvocationHandler targetProxy = new TargetProxy(target);
    common(targetProxy);
  }

  public void common(InvocationHandler targetProxy){
    // hook 是被钩对象
    Target hook = (Target) Proxy.newProxyInstance(Target.class.getClassLoader(), new Class[]{Target.class}, targetProxy);
    // 被钩对象执行时 先去执行钩子函数逻辑（TargetProxy#invoke）
    System.out.println(hook.execute("goat"));
  }

  @Test
  public void test4(){
    //返回的是代理对象，实现了Target接口，
    //实际调用方法的时候，是调用TargetProxy的invoke()方法
    Target hook = (Target) TargetProxy.wrap(target);
    // 调用顺序：TargetProxy#invoke(Object proxy, Method method, Object[] args)  ---> Target#String execute(String name);
    hook.execute(" HelloWord ");
  }

}
