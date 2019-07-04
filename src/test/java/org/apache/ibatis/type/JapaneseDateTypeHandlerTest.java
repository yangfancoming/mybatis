
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.chrono.JapaneseDate;

import org.junit.jupiter.api.Test;

class JapaneseDateTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<JapaneseDate> TYPE_HANDLER = new JapaneseDateTypeHandler();
  private static final JapaneseDate JAPANESE_DATE = JapaneseDate.now();
  private static final Date DATE = Date.valueOf(LocalDate.ofEpochDay(JAPANESE_DATE.toEpochDay()));

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, JAPANESE_DATE, null);
    verify(ps).setDate(1, DATE);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getDate("column")).thenReturn(DATE);
    assertEquals(JAPANESE_DATE, TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getDate("column")).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getDate(1)).thenReturn(DATE);
    assertEquals(JAPANESE_DATE, TYPE_HANDLER.getResult(rs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getDate(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getDate(1)).thenReturn(DATE);
    assertEquals(JAPANESE_DATE, TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getDate(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }

}
