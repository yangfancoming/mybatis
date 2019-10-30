package org.apache.goat.chapter200.D07;



import org.apache.goat.chapter200.D05.Target;
import org.apache.goat.chapter200.D05.TargetImpl;
import org.junit.Test;

/**

 * 这样就能实现前后拦截，并且拦截器能获取拦截对象信息，这样扩展性就好很多了。
 * 但是测试例子的这样调用看着很别扭，
 * 对应目标类来说，只需要了解对他插入了什么拦截就好。再修改一下，在拦截器增加一个插入目标类的方法
 * 详见：D08
 */
public class App {


  @Test
  public void test1(){
    Target target = new TargetImpl();
    Interceptor transactionInterceptor = new TransactionInterceptor();
    Target targetProxy = (Target) TargetProxy.wrap(target,transactionInterceptor);
    targetProxy.execute(" HelloWord ");
  }


}
