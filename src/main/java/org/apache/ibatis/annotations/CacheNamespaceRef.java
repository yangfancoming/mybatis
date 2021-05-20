
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @CacheNamespaceRef 指向指定命名空间的注解
 * The annotation that reference a cache.
 * If you use this annotation, should be specified either {@link #value()} or {@link #name()} attribute.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheNamespaceRef {

  /**
   * A namespace type to reference a cache (the namespace name become a FQCN of specified type).
   */
  Class<?> value() default void.class;

  /**
   * A namespace name to reference a cache.
   * @since 3.4.2
   */
  String name() default "";
}
