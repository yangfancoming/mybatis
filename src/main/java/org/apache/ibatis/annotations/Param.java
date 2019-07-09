
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 用于定义接口入参的别名，方便代码运行时获取接口入参，而不至于读取参数表中的参数时因为参数类型相同而导致参数获取紊乱。
 用法： List<T> selectByIdAndName (@Param("id") Integer id, @Param("name") String name);
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
  String value();
}
