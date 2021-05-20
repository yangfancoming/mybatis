
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

class ByteArrayTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<byte[]> TYPE_HANDLER = new ByteArrayTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, new byte[] { 1, 2, 3 }, null);
    verify(ps).setBytes(1, new byte[] { 1, 2, 3 });
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getBytes("column")).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    // Unnecessary
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getBytes(1)).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(rs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    // Unnecessary
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getBytes(1)).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }

  @Override
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    // Unnecessary
  }

}
