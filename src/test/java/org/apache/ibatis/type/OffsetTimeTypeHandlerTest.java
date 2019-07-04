
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetTime;

import org.junit.jupiter.api.Test;

class OffsetTimeTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<OffsetTime> TYPE_HANDLER = new OffsetTimeTypeHandler();

  private static final OffsetTime OFFSET_TIME = OffsetTime.now();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, OFFSET_TIME, null);
    verify(ps).setObject(1, OFFSET_TIME);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getObject("column", OffsetTime.class)).thenReturn(OFFSET_TIME);
    assertEquals(OFFSET_TIME, TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getObject("column", OffsetTime.class)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getObject(1, OffsetTime.class)).thenReturn(OFFSET_TIME);
    assertEquals(OFFSET_TIME, TYPE_HANDLER.getResult(rs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getObject(1, OffsetTime.class)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getObject(1, OffsetTime.class)).thenReturn(OFFSET_TIME);
    assertEquals(OFFSET_TIME, TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getObject(1, OffsetTime.class)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }
}
