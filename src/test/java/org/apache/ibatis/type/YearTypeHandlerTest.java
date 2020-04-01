
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Year;

import org.junit.jupiter.api.Test;


class YearTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Year> TYPE_HANDLER = new YearTypeHandler();
  private static final Year INSTANT = Year.now();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, INSTANT, null);
    verify(ps).setInt(1, INSTANT.getValue());
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getInt("column")).thenReturn(INSTANT.getValue());
    assertEquals(INSTANT, TYPE_HANDLER.getResult(rs, "column"));

    when(rs.getInt("column")).thenReturn(0);
    assertEquals(Year.of(0), TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getInt("column")).thenReturn(0);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getInt(1)).thenReturn(INSTANT.getValue());
    assertEquals(INSTANT, TYPE_HANDLER.getResult(rs, 1));

    when(rs.getInt(1)).thenReturn(0);
    assertEquals(Year.of(0), TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getInt(1)).thenReturn(0);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getInt(1)).thenReturn(INSTANT.getValue());
    assertEquals(INSTANT, TYPE_HANDLER.getResult(cs, 1));

    when(cs.getInt(1)).thenReturn(0);
    assertEquals(Year.of(0), TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getInt(1)).thenReturn(0);
    when(cs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
