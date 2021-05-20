
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 @ResultMap 结果集的注解
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultMap {
  String[] value();
}
