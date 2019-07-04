

package org.apache.ibatis.reflection;

import java.util.Optional;

/**
 * @deprecated Since 3.5.0, Will remove this class at future(next major version up).
 */
@Deprecated
public abstract class OptionalUtil {

  public static Object ofNullable(Object value) {
    return Optional.ofNullable(value);
  }

  private OptionalUtil() {
    super();
  }
}
