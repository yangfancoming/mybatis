
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class BigIntegerTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<BigInteger> TYPE_HANDLER = new BigIntegerTypeHandler();

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, new BigInteger("707070656505050302797979792923232303"), null);
    verify(ps).setBigDecimal(1, new BigDecimal("707070656505050302797979792923232303"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getBigDecimal("column")).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
    assertEquals(new BigInteger("707070656505050302797979792923232303"), TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getBigDecimal("column")).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getBigDecimal(1)).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
    assertEquals(new BigInteger("707070656505050302797979792923232303"), TYPE_HANDLER.getResult(rs,1 ));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getBigDecimal(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs,1 ));
    verify(rs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getBigDecimal(1)).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
    assertEquals(new BigInteger("707070656505050302797979792923232303"), TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getBigDecimal(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
    verify(cs, never()).wasNull();
  }

}
