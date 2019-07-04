
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class ShortTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Short> TYPE_HANDLER = new ShortTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, (short) 100, null);
    verify(ps).setShort(1, (short) 100);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getShort("column")).thenReturn((short) 100);
    assertEquals(Short.valueOf((short) 100), TYPE_HANDLER.getResult(rs, "column"));

    when(rs.getShort("column")).thenReturn((short) 0);
    assertEquals(Short.valueOf((short) 0), TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getShort("column")).thenReturn((short) 0);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getShort(1)).thenReturn((short) 100);
    assertEquals(Short.valueOf((short) 100), TYPE_HANDLER.getResult(rs, 1));

    when(rs.getShort(1)).thenReturn((short) 0);
    assertEquals(Short.valueOf((short) 0), TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getShort(1)).thenReturn((short) 0);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getShort(1)).thenReturn((short) 100);
    assertEquals(Short.valueOf((short) 100), TYPE_HANDLER.getResult(cs, 1));

    when(cs.getShort(1)).thenReturn((short) 0);
    assertEquals(Short.valueOf((short) 0), TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getShort(1)).thenReturn((short) 0);
    when(cs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
