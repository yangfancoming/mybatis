
package org.apache.ibatis.logging;

/**
 mybatis  目标接口   期待第三方日志组件 来实现它
*/
public interface Log {

  boolean isDebugEnabled();

  boolean isTraceEnabled();

  void error(String s, Throwable e);

  void error(String s);

  void debug(String s);

  void trace(String s);

  void warn(String s);

}
