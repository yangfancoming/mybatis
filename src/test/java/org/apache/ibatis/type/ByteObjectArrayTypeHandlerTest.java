
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ByteObjectArrayTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Byte[]> TYPE_HANDLER = new ByteObjectArrayTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, new Byte[] { 1, 2, 3 }, null);
    verify(ps).setBytes(1, new byte[] { 1, 2, 3 });
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    byte[] byteArray = new byte[] { 1, 2 };
    when(rs.getBytes("column")).thenReturn(byteArray);
    assertThat(TYPE_HANDLER.getResult(rs, "column")).isEqualTo(new Byte[]{1, 2});
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getBytes("column")).thenReturn(null);
    assertThat(TYPE_HANDLER.getResult(rs, "column")).isNull();
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    byte[] byteArray = new byte[] { 1, 2 };
    when(rs.getBytes(1)).thenReturn(byteArray);
    assertThat(TYPE_HANDLER.getResult(rs, 1)).isEqualTo(new Byte[]{1, 2});
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getBytes(1)).thenReturn(null);
    assertThat(TYPE_HANDLER.getResult(rs, 1)).isNull();
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    byte[] byteArray = new byte[] { 1, 2 };
    when(cs.getBytes(1)).thenReturn(byteArray);
    assertThat(TYPE_HANDLER.getResult(cs, 1)).isEqualTo(new Byte[]{1, 2});
    verify(cs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getBytes(1)).thenReturn(null);
    assertThat(TYPE_HANDLER.getResult(cs, 1)).isNull();
    verify(cs, never()).wasNull();
  }

}
