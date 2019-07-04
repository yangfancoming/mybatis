
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.Reader;
import java.sql.Clob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NClobTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<String> TYPE_HANDLER = new NClobTypeHandler();

  @Mock
  protected Clob clob;

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, "Hello", null);
    verify(ps).setCharacterStream(Mockito.eq(1), Mockito.any(Reader.class), Mockito.eq(5));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getClob("column")).thenReturn(clob);
    when(clob.length()).thenReturn(3l);
    when(clob.getSubString(1, 3)).thenReturn("Hello");
    assertEquals("Hello", TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getClob("column")).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getClob(1)).thenReturn(clob);
    when(clob.length()).thenReturn(3L);
    when(clob.getSubString(1, 3)).thenReturn("Hello");
    assertEquals("Hello", TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getClob(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getClob(1)).thenReturn(clob);
    when(clob.length()).thenReturn(3L);
    when(clob.getSubString(1, 3)).thenReturn("Hello");
    assertEquals("Hello", TYPE_HANDLER.getResult(cs, 1));
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getClob(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
