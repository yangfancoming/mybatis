
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 @Results 对应XML中的 标签为 <resultMap />，用来表示结果集和行结果的映射关系
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Results {
  /**
   * The name of the result map.
   */
  String id() default "";
  Result[] value() default {};
}
