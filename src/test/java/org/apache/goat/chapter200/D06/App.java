package org.apache.goat.chapter200.D06;

import org.apache.goat.chapter200.D05.Target;
import org.apache.goat.chapter200.D05.TargetImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 现在可以根据需要动态的添加拦截器了，在每次执行业务代码execute（...）之前都会拦截，看起来高级一丢丢了，来测试一下
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
