package org.apache.goat.chapter200.D08;


import org.apache.goat.chapter200.D05.Target;
import org.apache.goat.chapter200.D05.TargetImpl;
import org.junit.Test;

/**
 * 到这里就差不多完成了，可能有同学可能会有疑问，那我要添加多个拦截器呢，怎么搞？ ---> test2()
 */
public class App {

  Target target = new TargetImpl();

  @Test
  public void test1(){
    Interceptor transactionInterceptor = new TransactionInterceptor();
    //把事务拦截器插入到目标类中
    target = (Target) transactionInterceptor.plugin(target);
    target.execute(" HelloWord ");
  }

  @Test
  public void test2(){
    Interceptor transactionInterceptor = new TransactionInterceptor();
    target = (Target) transactionInterceptor.plugin(target);
    target.execute(" 11111 ");
    System.out.println("-----------------------");
    Interceptor logInterceptor = new LogInterceptor();
    target = (Target)logInterceptor.plugin(target);
    target.execute(" 222222 ");
  }

}
