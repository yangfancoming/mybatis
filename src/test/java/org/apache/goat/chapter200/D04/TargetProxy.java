package org.apache.goat.chapter200.D04;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by 64274 on 2019/10/29.
 * @ Description: 自定义代理需要实现InvocationHandler接口
 * @ author  山羊来了
 * @ date 2019/10/29---19:31
 */
public class TargetProxy implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    System.out.println(" 前置处理。。。");
    return args[0];
  }

}
