
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class FloatTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Float> TYPE_HANDLER = new FloatTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, 100f, null);
    verify(ps).setFloat(1, 100f);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getFloat("column")).thenReturn(100f);
    assertEquals(Float.valueOf(100f), TYPE_HANDLER.getResult(rs, "column"));

    when(rs.getFloat("column")).thenReturn(0f);
    assertEquals(Float.valueOf(0f), TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getFloat("column")).thenReturn(0f);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getFloat(1)).thenReturn(100f);
    assertEquals(Float.valueOf(100f), TYPE_HANDLER.getResult(rs, 1));

    when(rs.getFloat(1)).thenReturn(0f);
    assertEquals(Float.valueOf(0f), TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getFloat(1)).thenReturn(0f);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getFloat(1)).thenReturn(100f);
    assertEquals(Float.valueOf(100f), TYPE_HANDLER.getResult(cs, 1));

    when(cs.getFloat(1)).thenReturn(0f);
    assertEquals(Float.valueOf(0f), TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getFloat(1)).thenReturn(0f);
    when(cs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
