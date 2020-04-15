package org.apache.goat.chapter200.D09;

import org.apache.goat.chapter200.D07.Invocation;

/**
 * Created by 64274 on 2019/10/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/30---20:22
 */
public interface Interceptor {
  /**
   * 具体拦截处理
   * @param invocation
   * @return
   * @throws Exception
   */
  Object intercept(Invocation invocation) throws Exception;

  /**
   *  插入目标类
   * @param target
   * @return
   */
  Object plugin(Object target);

}
