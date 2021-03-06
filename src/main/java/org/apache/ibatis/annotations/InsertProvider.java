
package org.apache.ibatis.annotations;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InsertProvider {

  /**
   * Specify a type that implements an SQL provider method.
   * @return a type that implements an SQL provider method
   * @since 3.5.2
   * @see #type()
   */
  Class<?> value() default void.class;

  /**
   * Specify a type that implements an SQL provider method.
   * This attribute is alias of {@link #value()}.
   * @return a type that implements an SQL provider method
   * @see #value()
   */
  Class<?> type() default void.class;

  /**
   * Specify a method for providing an SQL.
   * Since 3.5.1, this attribute can omit.
   * If this attribute omit, the MyBatis will call a method that decide by following rules.
   *     If class that specified the {@link #type()} attribute implements the {@link org.apache.ibatis.builder.annotation.ProviderMethodResolver},
   *     the MyBatis use a method that returned by it
   *     If cannot resolve a method by {@link org.apache.ibatis.builder.annotation.ProviderMethodResolver}(= not implement it or it was returned {@code null}),
   *     the MyBatis will search and use a fallback method that named {@code provideSql} from specified type
   * @return a method name of method for providing an SQL
   */
  String method() default "";

}
