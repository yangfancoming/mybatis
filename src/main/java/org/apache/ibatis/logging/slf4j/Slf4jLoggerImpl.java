
package org.apache.ibatis.logging.slf4j;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;


/**
 * 适配者，将目标接口Log的方法调用转换为Slf4jLoggerImpl自身log实例的方法调用
 */
class Slf4jLoggerImpl implements Log {

  private final Logger log;

  public Slf4jLoggerImpl(Logger logger) {
    log = logger;
  }

  /**
   * 实现Log接口，并重写对应的方法，在方法内部调用的是slf4j的日志实现，
   */
  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  @Override
  public void error(String s, Throwable e) {
    log.error(s, e);
  }

  @Override
  public void error(String s) {
    log.error(s);
  }

  @Override
  public void debug(String s) {
    log.debug(s);
  }

  @Override
  public void trace(String s) {
    log.trace(s);
  }

  @Override
  public void warn(String s) {
    log.warn(s);
  }

}
