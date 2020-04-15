package org.apache.goat.chapter200.D09;


import org.apache.goat.chapter200.D05.Target;
import org.apache.goat.chapter200.D05.TargetImpl;

import org.junit.Test;

/**
 * 其实上面已经实现的没问题了，只是还差那么一点点，添加多个拦截器的时候不太美观，
 * 让我们再次利用面向对象思想封装一下。我们设计一个InterceptorChain 拦截器链类
 * 其实就是通过pluginAll() 方法包一层把所有的拦截器插入到目标类去而已
 */
public class App {

  Target target = new TargetImpl();

  @Test
  public void test1(){

    InterceptorChain interceptorChain = new InterceptorChain();
    interceptorChain.addInterceptor(new TransactionInterceptor()).addInterceptor(new LogInterceptor());
//    interceptorChain.addInterceptor(new LogInterceptor()).addInterceptor(new TransactionInterceptor());

    target = (Target) interceptorChain.pluginAll(target);
    target.execute("111111");
  }

}
