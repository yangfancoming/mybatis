package org.apache.goat.chapter200.D09;


import org.apache.goat.chapter200.D07.Invocation;

/**
 * Created by 64274 on 2019/10/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/30---20:22
 */
public class TransactionInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Exception{
    System.out.println(" 开启事务 ");
    Object result = invocation.process();
    System.out.println(" 提交事务 ");
    return result;
  }

  @Override
  public Object plugin(Object target) {
    return TargetProxy.wrap(target,this);
  }
}
