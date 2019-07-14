
package org.apache.ibatis.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j.Log4jImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

class LogFactoryTest {

  /**
   [2019-07-13 19:41:44,197]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.slf4j.Slf4jImpl' adapter.
   [2019-07-13 19:41:44,214]org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.debug(JakartaCommonsLoggingImpl.java:38)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl' adapter.
   [2019-07-13 19:41:44,214]org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.warn(JakartaCommonsLoggingImpl.java:48) WARN:Warning message.
   [2019-07-13 19:41:44,215]org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.debug(JakartaCommonsLoggingImpl.java:38)DEBUG:Debug message.
   [2019-07-13 19:41:44,215]org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.error(JakartaCommonsLoggingImpl.java:33)ERROR:Error message.
   [2019-07-13 19:41:44,216]org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.error(JakartaCommonsLoggingImpl.java:28)ERROR:Error with Exception.
  */
  @Test
  void shouldUseCommonsLogging() {
    LogFactory.useCommonsLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), JakartaCommonsLoggingImpl.class.getName());
  }

  /**
   [2019-07-13 19:41:20,508]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.slf4j.Slf4jImpl' adapter.
   [2019-07-13 19:41:20,511]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.log4j.Log4jImpl' adapter.
   [2019-07-13 19:41:20,512]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:90) WARN:Warning message.
   [2019-07-13 19:41:20,512]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:91)DEBUG:Debug message.
   [2019-07-13 19:41:20,512]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:92)ERROR:Error message.
   [2019-07-13 19:41:20,513]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:93)ERROR:Error with Exception.
  */
  @Test
  void shouldUseLog4J() {
    LogFactory.useLog4JLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Log4jImpl.class.getName());
  }

  /**
   [2019-07-13 19:42:07,832]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.slf4j.Slf4jImpl' adapter.
   ERROR StatusLogger No Log4j 2 configuration file found. Using default configuration (logging only errors to the console), or user programmatically provided configurations. Set system property 'log4j2.debug' to show Log4j 2 internal initialization logging. See https://logging.apache.org/log4j/2.x/manual/configuration.html for instructions on how to configure Log4j 2
   19:42:08.198 [main] ERROR java.lang.Object - Error message.
   19:42:08.203 [main] ERROR java.lang.Object - Error with Exception.
  */
  @Test
  void shouldUseLog4J2() {
    LogFactory.useLog4J2Logging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Log4j2Impl.class.getName());
  }

  /**
   7月 14, 2019 12:48:38 下午 org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl warn
   警告: Warning message.
   7月 14, 2019 12:48:38 下午 org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl error
   严重: Error message.
   7月 14, 2019 12:48:38 下午 org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl error
   严重: Error with Exception.
  */
  @Test
  void shouldUseJdKLogging() {
    LogFactory.useJdkLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Jdk14LoggingImpl.class.getName());
  }
  /**
   [2019-07-13 19:42:47,852]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.slf4j.Slf4jImpl' adapter.
   [2019-07-13 19:42:47,855]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.slf4j.Slf4jImpl' adapter.
   [2019-07-13 19:42:47,856]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:111) WARN:Warning message.
   [2019-07-13 19:42:47,856]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:112)DEBUG:Debug message.
   [2019-07-13 19:42:47,856]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:113)ERROR:Error message.
   [2019-07-13 19:42:47,857]org.apache.ibatis.logging.LogFactoryTest.logSomething(LogFactoryTest.java:114)ERROR:Error with Exception.
  */
  @Test
  void shouldUseSlf4j() {
    LogFactory.useSlf4jLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Slf4jImpl.class.getName());
  }

  /**
   [2019-07-13 19:43:18,817]org.apache.ibatis.logging.LogFactory.setImplementation(LogFactory.java:116)DEBUG:Logging initialized using 'class org.apache.ibatis.logging.slf4j.Slf4jImpl' adapter.
   Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
   Warning message.Error message.
   Debug message.
   Error with Exception.
  */
  @Test
  void shouldUseStdOut() {
    LogFactory.useStdOutLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), StdOutImpl.class.getName());
  }

  @Test
  void shouldUseNoLogging() {
    LogFactory.useNoLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), NoLoggingImpl.class.getName());
  }

  // 将从 mybatis-config.xml 读取 NO_LOGGING 设置
  @Test
  void shouldReadLogImplFromSettings() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/logging/mybatis-config.xml")) {
      new SqlSessionFactoryBuilder().build(reader);
    }
    Log log = LogFactory.getLog(Object.class);
    log.debug("Debug message.");
    assertEquals(log.getClass().getName(), NoLoggingImpl.class.getName());
  }

  private void logSomething(Log log) {
    log.warn("Warning message.");
    log.debug("Debug message.");
    log.error("Error message.");
    log.error("Error with Exception.", new Exception("Test exception."));
  }

}
