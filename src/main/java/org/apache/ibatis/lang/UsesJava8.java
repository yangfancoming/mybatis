
package org.apache.ibatis.lang;

import java.lang.annotation.*;

/**
 * Indicates that the element uses Java 8 API.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface UsesJava8 {
}
