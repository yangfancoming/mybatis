
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that inject a property value.
 *
 * @since 3.4.2
 * @author Kazuki Shimizu
 * @see CacheNamespace
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Property {

  /**
   * A target property name.
   */
  String name();

  /**
   * A property value or placeholder.
   */
  String value();
}
