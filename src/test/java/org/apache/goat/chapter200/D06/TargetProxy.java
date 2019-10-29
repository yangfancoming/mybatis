package org.apache.goat.chapter200.D06;

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

  private Object target;

  // 存放 所有实现Interceptor接口的实现类集合
  private List<Interceptor> interceptorList;

  public TargetProxy(Object target,List<Interceptor> interceptorList) {
    this.target = target;
    this.interceptorList = interceptorList;
  }


  /**
   * @Description: 包装
   * @author fan.yang
   * @date 2019年10月27日21:11:26
   * @param target 接口实现类
   * @param method public abstract java.lang.String org.apache.goat.chapter200.D05.Target.execute(java.lang.String)
   * @param args "HelloWord"
   * @return 成功包装后的对象
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    // 遍历所有接口的实现类 循环调用实现类重写的 intercept()方法
    for (Interceptor interceptor : interceptorList) {
      interceptor.intercept();
    }
    return method.invoke(target, args);
  }

  public static Object wrap(Object target,List<Interceptor> interceptorList) {
    InvocationHandler targetProxy = new TargetProxy(target, interceptorList);
    return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),targetProxy);
  }
}
