
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * This annotation can be used when a @Select method is using a ResultHandler.
 * Those methods must have void return type, so this annotation can be used to tell MyBatis what kind of object it should build for each row.
 * @since 3.2.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultType {
  Class<?> value();
}
