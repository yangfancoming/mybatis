
package org.apache.ibatis.logging.jdbc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StatementLoggerTest {

  @Mock
  Statement statement;

  @Mock
  Log log;

  private Statement st;

  @BeforeEach
  void setUp() throws SQLException {
    st = StatementLogger.newInstance(statement, log, 1);
  }

  @Test
  void shouldPrintLog() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    st.executeQuery("select 1");

    verify(log).debug(contains("Executing: select 1"));
  }

  @Test
  void shouldPrintLogForUpdate() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    when(statement.execute(anyString())).thenReturn(true);
    String sql = "update name = '' from test";
    boolean execute = st.execute(sql);

    verify(log).debug(contains(sql));
    Assertions.assertTrue(execute);
  }

  @Test
  void shouldNotPrintLog() throws SQLException {
    st.getResultSet();
    st.close();
    verify(log, times(0)).debug(anyString());
  }
}
