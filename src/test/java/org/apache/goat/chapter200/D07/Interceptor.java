package org.apache.goat.chapter200.D07;


/**
 * Created by 64274 on 2019/10/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/30---19:23
 */
public interface Interceptor {
  /**
   * 具体拦截处理
   * @param invocation
   * @throws Exception
   */
  Object intercept(Invocation invocation) throws Exception;

}
