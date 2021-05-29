package org.apache.goat.chapter200.D04;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理 只代理接口 没有实现类的情况  (mybatis就是只有接口没有实现类)
 */
public class App {

  @Test
  public void test(){
    InvocationHandler targetProxy = new TargetProxy();
    // hook 是被钩对象
    Target hook = (Target) Proxy.newProxyInstance(Target.class.getClassLoader(), new Class[]{Target.class}, targetProxy);
    System.out.println("返回值为：" + hook.test("goat"));
  }

}
