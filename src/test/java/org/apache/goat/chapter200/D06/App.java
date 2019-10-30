package org.apache.goat.chapter200.D06;

import org.apache.goat.chapter200.D05.Target;
import org.apache.goat.chapter200.D05.TargetImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 现在可以根据需要动态的添加拦截器了，在每次执行业务代码execute（...）之前都会拦截，看起来高级一丢丢了，来测试一下
 *
 * 貌似有哪里不太对一样，按照上面这种我们只能做前置拦截，而且拦截器并不知道拦截对象的信息。
 * 应该做更一步的抽象，把拦截对象信息进行封装，作为拦截器拦截方法的参数，
 * 把拦截目标对象真正的执行方法放到Interceptor中完成，这样就可以实现前后拦截，
 * 并且还能对拦截对象的参数等做修改。设计一个Invocation 对象  详见：D07
 */
public class App {


  @Test
  public void test1(){
    List<Interceptor> interceptorList = new ArrayList<>();
    interceptorList.add(new LogInterceptor());
    interceptorList.add(new TransactionInterceptor());

    Target target = new TargetImpl();
    Target targetProxy = (Target) TargetProxy.wrap(target,interceptorList);
    targetProxy.execute(" HelloWord ");
  }


}
