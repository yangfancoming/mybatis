
package org.apache.ibatis.reflection;

import org.apache.ibatis.io.Resources;

/**
 * To check the existence of version dependent classes.
 */
public class Jdk {

  /**
   * <code>true</code> if <code>java.lang.reflect.Parameter</code> is available.
   * @deprecated Since 3.5.0, Will remove this field at feature(next major version up)
   */
  @Deprecated
  public static final boolean parameterExists;

  static {
    boolean available = false;
    try {
      Resources.classForName("java.lang.reflect.Parameter");
      available = true;
    } catch (ClassNotFoundException e) {
      // ignore
    }
    parameterExists = available;
  }

  /**
   * @deprecated Since 3.5.0, Will remove this field at feature(next major version up)
   */
  @Deprecated
  public static final boolean dateAndTimeApiExists;
  static {
    boolean available = false;
    try {
      Resources.classForName("java.time.Clock");
      available = true;
    } catch (ClassNotFoundException e) {
      // ignore
    }
    dateAndTimeApiExists = available;
  }

  /**
   * @deprecated Since 3.5.0, Will remove this field at feature(next major version up)
   */
  @Deprecated
  public static final boolean optionalExists;

  static {
    boolean available = false;
    try {
      Resources.classForName("java.util.Optional");
      available = true;
    } catch (ClassNotFoundException e) {
      // ignore
    }
    optionalExists = available;
  }

  private Jdk() {
    super();
  }
}
