
package org.apache.ibatis.logging.jdbc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultSetLoggerTest {

  @Mock
  private ResultSet rs;

  @Mock
  private Log log;

  @Mock
  private ResultSetMetaData metaData;

  private void setup(int type) throws SQLException {
    when(rs.next()).thenReturn(true);
    when(rs.getMetaData()).thenReturn(metaData);
    when(metaData.getColumnCount()).thenReturn(1);
    when(metaData.getColumnType(1)).thenReturn(type);
    when(metaData.getColumnLabel(1)).thenReturn("ColumnName");
    when(log.isTraceEnabled()).thenReturn(true);
    ResultSet resultSet = ResultSetLogger.newInstance(rs, log, 1);
    resultSet.next();
  }

  @Test
  void shouldNotPrintBlobs() throws SQLException {
    setup(Types.LONGNVARCHAR);
    verify(log).trace("<==    Columns: ColumnName");
    verify(log).trace("<==        Row: <<BLOB>>");
  }

  @Test
  void shouldPrintVarchars() throws SQLException {
    when(rs.getString(1)).thenReturn("value");
    setup(Types.VARCHAR);
    verify(log).trace("<==    Columns: ColumnName");
    verify(log).trace("<==        Row: value");
  }

}
