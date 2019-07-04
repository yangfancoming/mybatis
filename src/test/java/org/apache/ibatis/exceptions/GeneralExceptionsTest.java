
package org.apache.ibatis.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.logging.LogException;
import org.apache.ibatis.parsing.ParsingException;
import org.apache.ibatis.plugin.PluginException;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.scripting.ScriptingException;
import org.apache.ibatis.session.SqlSessionException;
import org.apache.ibatis.transaction.TransactionException;
import org.apache.ibatis.type.TypeException;
import org.junit.jupiter.api.Test;

class GeneralExceptionsTest {

  private static final String EXPECTED_MESSAGE = "Test Message";
  private static final Exception EXPECTED_CAUSE = new Exception("Nested Exception");

  @Test
  void should() {
    RuntimeException thrown = ExceptionFactory.wrapException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
    assertTrue(thrown instanceof PersistenceException, "Exception should be wrapped in RuntimeSqlException.");
    testThrowException(thrown);
  }

  @Test
  void shouldInstantiateAndThrowAllCustomExceptions() throws Exception {
    Class<?>[] exceptionTypes = {
        BindingException.class,
        CacheException.class,
        DataSourceException.class,
        ExecutorException.class,
        LogException.class,
        ParsingException.class,
        BuilderException.class,
        PluginException.class,
        ReflectionException.class,
        PersistenceException.class,
        SqlSessionException.class,
        TransactionException.class,
        TypeException.class,
        ScriptingException.class
    };
    for (Class<?> exceptionType : exceptionTypes) {
      testExceptionConstructors(exceptionType);
    }

  }

  private void testExceptionConstructors(Class<?> exceptionType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Exception e = (Exception) exceptionType.newInstance();
    testThrowException(e);
    e = (Exception) exceptionType.getConstructor(String.class).newInstance(EXPECTED_MESSAGE);
    testThrowException(e);
    e = (Exception) exceptionType.getConstructor(String.class, Throwable.class).newInstance(EXPECTED_MESSAGE, EXPECTED_CAUSE);
    testThrowException(e);
    e = (Exception) exceptionType.getConstructor(Throwable.class).newInstance(EXPECTED_CAUSE);
    testThrowException(e);
  }

  private void testThrowException(Exception thrown) {
    try {
      throw thrown;
    } catch (Exception caught) {
      assertEquals(thrown.getMessage(), caught.getMessage());
      assertEquals(thrown.getCause(), caught.getCause());
    }
  }

}
