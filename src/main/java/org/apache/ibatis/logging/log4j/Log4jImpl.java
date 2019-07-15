
package org.apache.ibatis.logging.log4j;

import org.apache.ibatis.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * log4j包下的Log4jImpl 使用 Log4J 包实现了 Log接口
 * 我们再看一个相对简单的，Log4jImpl是Mybatis的Log到log4j的适配器，只有一个类就搞定了。
 * 这里的Log4jImpl类就是适配器，它实现了Log接口，而适配者就是构造方法的创建的log对象，因此很简单。
 * 适配器，将目标接口Log的方法调用转换为Logger自身log实例的方法调用
 */
public class Log4jImpl implements Log {

  private static final String FQCN = Log4jImpl.class.getName();

  private final Logger log;

  public Log4jImpl(String clazz) {
    log = Logger.getLogger(clazz);
  }

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
    log.log(FQCN, Level.ERROR, s, e);
  }

  @Override
  public void error(String s) {
    log.log(FQCN, Level.ERROR, s, null);
  }

  @Override
  public void debug(String s) {
    log.log(FQCN, Level.DEBUG, s, null);
  }

  @Override
  public void trace(String s) {
    log.log(FQCN, Level.TRACE, s, null);
  }

  @Override
  public void warn(String s) {
    log.log(FQCN, Level.WARN, s, null);
  }

}
