package org.apache.goat.chapter200.D08;


import org.apache.goat.chapter200.D07.Invocation;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/29---21:40
 */
public class LogInterceptor implements Interceptor {

  /**
   * 具体拦截处理
   * @param invocation
   * @throws Exception
   */
  @Override
  public Object intercept(Invocation invocation) throws Exception {
    System.out.println(" 记录日志前 ");
    Object result = invocation.process();
    System.out.println(" 记录日志后 ");
    return result;
  }

  /**
   * 插入目标类
   * @param target
   */
  @Override
  public Object plugin(Object target) {
    return TargetProxy.wrap(target,this);
  }
}
