
package org.apache.ibatis.annotations;

import org.apache.ibatis.mapping.FetchType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 @Many 复杂类型的集合属性值的注解
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Many {
  String select() default "";

  FetchType fetchType() default FetchType.DEFAULT;

}
