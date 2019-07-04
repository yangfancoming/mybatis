
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NStringTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<String> TYPE_HANDLER = new NStringTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, "Hello", null);
    verify(ps).setNString(1, "Hello");
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getNString("column")).thenReturn("Hello");
    assertEquals("Hello", TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    // Unnecessary
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getNString(1)).thenReturn("Hello");
    assertEquals("Hello", TYPE_HANDLER.getResult(rs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    // Unnecessary
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getNString(1)).thenReturn("Hello");
    assertEquals("Hello", TYPE_HANDLER.getResult(cs, 1));
    verify(rs, never()).wasNull();
  }

  @Override
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    // Unnecessary
  }

}
