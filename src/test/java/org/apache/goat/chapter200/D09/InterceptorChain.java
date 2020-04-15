package org.apache.goat.chapter200.D09;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2020/4/15.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/15---16:09
 */
public class InterceptorChain {

  private List<Interceptor> interceptorList = new ArrayList<>();

  /**
   * 插入所有拦截器
   */
  public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptorList) {
      target = interceptor.plugin(target);
    }
    return target;
  }

  public InterceptorChain addInterceptor(Interceptor interceptor) {
    interceptorList.add(interceptor);
    return this;
  }

  /**
   * 返回一个不可修改集合，只能通过addInterceptor方法添加，这样控制权就在自己手里
   */
  public List<Interceptor> getInterceptorList() {
    return Collections.unmodifiableList(interceptorList);
  }
}
