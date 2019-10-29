package org.apache.goat.chapter200.D06;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/29---21:40
 */
public class LogInterceptor implements Interceptor {

  @Override
  public void intercept() {
    System.out.println(" 记录日志 ");
  }
}
