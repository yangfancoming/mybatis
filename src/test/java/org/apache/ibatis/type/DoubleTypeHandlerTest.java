
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoubleTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Double> TYPE_HANDLER = new DoubleTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, 100d, null);
    verify(ps).setDouble(1, 100d);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getDouble("column")).thenReturn(100d);
    assertEquals(Double.valueOf(100d), TYPE_HANDLER.getResult(rs, "column"));

    when(rs.getDouble("column")).thenReturn(0d);
    assertEquals(Double.valueOf(0d), TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getDouble("column")).thenReturn(0d);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getDouble(1)).thenReturn(100d);
    assertEquals(Double.valueOf(100d), TYPE_HANDLER.getResult(rs, 1));

    when(rs.getDouble(1)).thenReturn(0d);
    assertEquals(Double.valueOf(0d), TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getDouble(1)).thenReturn(0d);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getDouble(1)).thenReturn(100d);
    assertEquals(Double.valueOf(100d), TYPE_HANDLER.getResult(cs, 1));

    when(cs.getDouble(1)).thenReturn(0d);
    assertEquals(Double.valueOf(0d), TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getDouble(1)).thenReturn(0d);
    when(cs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
