
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 @MapKey Map 结果的键的注解
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapKey {
  String value();
}
