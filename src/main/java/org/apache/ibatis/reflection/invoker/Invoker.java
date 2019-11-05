
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * 通过该封装之后， 本来需要声明 Map<String, Method> 和 Map<String, Field> 表示的， 只需要使用 Map<String, Invoker> 即可表示
 * MethodInvoker: 方法的Invoker
 * GetFieldInvoker: 如果没有setter, 则使用该方法， 通过Filed类直接设置成员变量的值
 * SetFieldInvoker: 如果没有getter， 则使用该方法， 通过Field类直接读取成员变量的值
*/
public interface Invoker {

  // 调用方法
  Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

  // 获取类型
  Class<?> getType();
}
