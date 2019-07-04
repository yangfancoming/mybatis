
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.sql.Blob;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class BlobTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<byte[]> TYPE_HANDLER = new BlobTypeHandler();

  @Mock
  protected Blob blob;

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, new byte[] { 1, 2, 3 }, null);
    verify(ps).setBinaryStream(Mockito.eq(1), Mockito.any(InputStream.class), Mockito.eq(3));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getBlob("column")).thenReturn(blob);
    when(blob.length()).thenReturn(3l);
    when(blob.getBytes(1, 3)).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getBlob("column")).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getBlob(1)).thenReturn(blob);
    when(blob.length()).thenReturn(3l);
    when(blob.getBytes(1, 3)).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getBlob(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getBlob(1)).thenReturn(blob);
    when(blob.length()).thenReturn(3l);
    when(blob.getBytes(1, 3)).thenReturn(new byte[] { 1, 2, 3 });
    assertArrayEquals(new byte[] { 1, 2, 3 }, TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getBlob(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
