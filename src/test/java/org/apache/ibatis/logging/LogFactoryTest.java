
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

  @Test
  void shouldUseCommonsLogging() {
    LogFactory.useCommonsLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), JakartaCommonsLoggingImpl.class.getName());
  }

  @Test
  void shouldUseLog4J() {
    LogFactory.useLog4JLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Log4jImpl.class.getName());
  }

  @Test
  void shouldUseLog4J2() {
    LogFactory.useLog4J2Logging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Log4j2Impl.class.getName());
  }

  @Test
  void shouldUseJdKLogging() {
    LogFactory.useJdkLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Jdk14LoggingImpl.class.getName());
  }

  @Test
  void shouldUseSlf4j() {
    LogFactory.useSlf4jLogging();
    Log log = LogFactory.getLog(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Slf4jImpl.class.getName());
  }

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
