package org.apache.goat.chapter200.D06;

import org.apache.goat.chapter200.D06.interceptor.Interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/29---21:41
 */
public class TargetProxy implements InvocationHandler {

  // 存放 被代理的接口实现类
  private Object target;

  // 存放 所有实现Interceptor接口的实现类集合
  private List<Interceptor> interceptorList;

  public TargetProxy(Object target,List<Interceptor> interceptorList) {
    this.target = target;
    this.interceptorList = interceptorList;
  }

  /**
   * @Description: 包装
   * @date 2019年10月27日21:11:26
   * @param method public abstract java.lang.String org.apache.goat.chapter200.D05.Target.execute(java.lang.String)
   * @param args "HelloWord"
   * @return 成功包装后的对象
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    /**
     *  遍历所有接口的实现类 循环调用实现类重写的 intercept()方法
     *  这样一来就把所有的要拦截处理的逻辑 都解耦的各个拦截器接口的实现类中去了，没有耦合在invoke方法里面了
    */
    for (Interceptor interceptor : interceptorList) {
      interceptor.intercept();
    }
    //通过反射来执行某个的对象的目标方法：总是获取先获取Method(对象的目标方法)，然后传入对应的Class实例对象和传入参数 来执行方法！
    return method.invoke(target, args);
  }

  public static Object wrap(Object target,List<Interceptor> interceptorList) {
    InvocationHandler targetProxy = new TargetProxy(target, interceptorList);
    return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),targetProxy);
  }

}
