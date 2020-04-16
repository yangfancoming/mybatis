
package org.apache.ibatis.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 签名  对应 Invocation 类记忆 三个参数  类、方法、参数
 * 就是定义哪些类，方法，参数需要被拦截
 * @see Invocation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Signature {

  // 指定要拦截的四大对象中的哪个类对象
  Class<?> type();

  // 指定要拦截 哪个方法
  String method();

  // 指定拦截方法的参数列表 （为了在函数重载时也能正确拦截）
  Class<?>[] args();
}
