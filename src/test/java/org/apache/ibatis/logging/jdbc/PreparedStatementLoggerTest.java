
package org.apache.ibatis.logging.jdbc;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreparedStatementLoggerTest {

  @Mock
  Log log;

  @Mock
  PreparedStatement preparedStatement;

  @Mock
  ResultSet resultSet;

  private PreparedStatement ps;

  @BeforeEach
  void setUp() throws SQLException {
    ps = PreparedStatementLogger.newInstance(this.preparedStatement, log, 1);
  }

  @Test
  void shouldPrintParameters() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    when(preparedStatement.executeQuery(anyString())).thenReturn(resultSet);

    ps.setInt(1, 10);
    ResultSet rs = ps.executeQuery("select 1 limit ?");

    verify(log).debug(contains("Parameters: 10(Integer)"));
    Assertions.assertNotNull(rs);
    Assertions.assertNotSame(resultSet, rs);
  }

  @Test
  void shouldPrintNullParameters() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    when(preparedStatement.execute(anyString())).thenReturn(true);

    ps.setNull(1, JdbcType.VARCHAR.TYPE_CODE);
    boolean result = ps.execute("update name = ? from test");

    verify(log).debug(contains("Parameters: null"));
    Assertions.assertTrue(result);
  }

  @Test
  void shouldNotPrintLog() throws SQLException {
    ps.getResultSet();
    ps.getParameterMetaData();

    verify(log, times(0)).debug(anyString());
  }

  @Test
  void shouldPrintUpdateCount() throws SQLException {
    when(log.isDebugEnabled()).thenReturn(true);
    when(preparedStatement.getUpdateCount()).thenReturn(1);

    ps.getUpdateCount();

    verify(log).debug(contains("Updates: 1"));
  }
}
