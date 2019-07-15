
package org.apache.ibatis.logging.nologging;

import org.apache.ibatis.logging.Log;

/**
 nologging包下的NoLoggingImpl 空实现了 Log接口，即Log接口的实现方法没有意义，或者没有任何代码。
*/
public class NoLoggingImpl implements Log {

  public NoLoggingImpl(String clazz) {
    // Do Nothing
  }

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void error(String s, Throwable e) {
    // Do Nothing
  }

  @Override
  public void error(String s) {
    // Do Nothing
  }

  @Override
  public void debug(String s) {
    // Do Nothing
  }

  @Override
  public void trace(String s) {
    // Do Nothing
  }

  @Override
  public void warn(String s) {
    // Do Nothing
  }

}
