
package org.apache.ibatis.logging.jdbc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectionLoggerTest {

  @Mock
  Connection connection;

  @Mock
  PreparedStatement preparedStatement;

  @Mock
  Log log;

  private Connection conn;

  @BeforeEach
  void setUp() throws SQLException {
    conn = ConnectionLogger.newInstance(connection, log, 1);
  }

  @Test
  void shouldPrintPrepareStatement() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    conn.prepareStatement("select 1");
    verify(log).debug(contains("Preparing: select 1"));
  }

  @Test
  void shouldPrintPrepareCall() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    conn.prepareCall("{ call test() }");
    verify(log).debug(contains("Preparing: { call test() }"));
  }

  @Test
  void shouldNotPrintCreateStatement() throws SQLException {
    conn.createStatement();
    conn.close();
    verify(log, times(0)).debug(anyString());
  }
}
