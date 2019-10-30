package org.apache.goat.chapter200.D07;

import java.lang.reflect.Method;

/**
 * Created by 64274 on 2019/10/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/30---19:46
 */
public class Invocation {

  // 目标对象
  private Object target;

  // 执行的方法
  private Method method;

  //方法的参数
  private Object[] args;


  public Invocation(Object target, Method method, Object[] args) {
    this.target = target;
    this.method = method;
    this.args = args;
  }


  // 执行目标对象的方法
  public Object process() throws Exception{
    return method.invoke(target,args);
  }
}
