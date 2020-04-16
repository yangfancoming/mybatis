
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * 拦截器接口，用户自定义的拦截器需要实现该接口
*/
public interface Interceptor {

  // 这个方法是拦截器的业务方法 就是实现要增强的功能；
  Object intercept(Invocation invocation) throws Throwable;

  // 这个方法是对拦截器的包装， 如果不包装的话它是不会被加入到拦截器链中 其实就是生成代理对象；
  /**
   * @Description: 包装
   * @date 2019年10月27日21:11:26
   * @param target 目标对象，需要被代理的对象
   * @return 成功包装后的对象
   */
  default Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  /**
   * 设置 <plugin> 标签下的所有 <property> 标签
   *     <plugin interceptor="org.apache.goat.chapter200.D10.MyFirstPlugin">
   *       <property name="username" value="goat"/>
   *       <property name="password" value="123654"/>
   *     </plugin>
  */
  default void setProperties(Properties properties) {
    // NOP
  }

}
