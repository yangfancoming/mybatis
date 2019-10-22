
package org.apache.ibatis.logging.jdk14;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ibatis.logging.Log;


/**
 jdk14包下的 Jdk14LoggingImpl 使用
 java.util.logging.Logger
 java.util.logging.Level
 2个类  实现了Log接口。
 jdkLog的适配器，实现的是Log接口（Mybatie自己规定的日志需要有的能力）
*/
public class Jdk14LoggingImpl implements Log {

  //真正提供日志能力的jdk的日志类
  // 真正提供日志能力的jdk的日志类（这就是引入的外部实现类）
  private final Logger log;

  public Jdk14LoggingImpl(String clazz) {
    log = Logger.getLogger(clazz);
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isLoggable(Level.FINE);
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isLoggable(Level.FINER);
  }

  @Override
  public void error(String s, Throwable e) {
    log.log(Level.SEVERE, s, e);
  }

  @Override
  public void error(String s) {
    log.log(Level.SEVERE, s);
  }

  @Override
  public void debug(String s) {
    log.log(Level.FINE, s);
  }

  @Override
  public void trace(String s) {
    log.log(Level.FINER, s);
  }

  @Override
  public void warn(String s) {
    log.log(Level.WARNING, s);
  }

}
