
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ByteTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Byte> TYPE_HANDLER = new ByteTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, (byte) 100, null);
    verify(ps).setByte(1, (byte) 100);
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getByte("column")).thenReturn((byte) 100);
    assertEquals(Byte.valueOf((byte) 100), TYPE_HANDLER.getResult(rs, "column"));

    when(rs.getByte("column")).thenReturn((byte) 0);
    assertEquals(Byte.valueOf((byte) 0), TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getByte("column")).thenReturn((byte) 0);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getByte(1)).thenReturn((byte) 100);
    assertEquals(Byte.valueOf((byte) 100), TYPE_HANDLER.getResult(rs, 1));

    when(rs.getByte(1)).thenReturn((byte) 0);
    assertEquals(Byte.valueOf((byte) 0), TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getByte(1)).thenReturn((byte) 0);
    when(rs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getByte(1)).thenReturn((byte) 100);
    assertEquals(Byte.valueOf((byte) 100), TYPE_HANDLER.getResult(cs, 1));

    when(cs.getByte(1)).thenReturn((byte) 0);
    assertEquals(Byte.valueOf((byte) 0), TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getByte(1)).thenReturn((byte) 0);
    when(cs.wasNull()).thenReturn(true);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
