
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import java.sql.Array;
import java.sql.Connection;
import java.sql.Types;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArrayTypeHandlerTest extends BaseTypeHandlerTest {

  private static final TypeHandler<Object> TYPE_HANDLER = new ArrayTypeHandler();

  @Mock
  Array mockArray;

  @Override
  @Test
  public void shouldSetParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, mockArray, null);
    verify(ps).setArray(1, mockArray);
  }
  
  @Test
  public void shouldSetStringArrayParameter() throws Exception {
    Connection connection = mock(Connection.class);
    when(ps.getConnection()).thenReturn(connection);
    
    Array array = mock(Array.class);
    when(connection.createArrayOf(anyString(), any(String[].class))).thenReturn(array);
    
    TYPE_HANDLER.setParameter(ps, 1, new String[] { "Hello World" }, JdbcType.ARRAY);
    verify(ps).setArray(1, array);
    verify(array).free();
  }
    
  @Test
  public void shouldSetNullParameter() throws Exception {
    TYPE_HANDLER.setParameter(ps, 1, null, JdbcType.ARRAY);
    verify(ps).setNull(1, Types.ARRAY);
  }

  @Test
  public void shouldFailForNonArrayParameter() {
    assertThrows(TypeException.class, () -> {
      TYPE_HANDLER.setParameter(ps, 1, "unsupported parameter type", null);
    });
  }
  
  @Override
  @Test
  public void shouldGetResultFromResultSetByName() throws Exception {
    when(rs.getArray("column")).thenReturn(mockArray);
    String[] stringArray = new String[]{"a", "b"};
    when(mockArray.getArray()).thenReturn(stringArray);
    assertEquals(stringArray, TYPE_HANDLER.getResult(rs, "column"));
    verify(mockArray).free();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByName() throws Exception {
    when(rs.getArray("column")).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, "column"));
  }

  @Override
  @Test
  public void shouldGetResultFromResultSetByPosition() throws Exception {
    when(rs.getArray(1)).thenReturn(mockArray);
    String[] stringArray = new String[]{"a", "b"};
    when(mockArray.getArray()).thenReturn(stringArray);
    assertEquals(stringArray, TYPE_HANDLER.getResult(rs, 1));
    verify(mockArray).free();
  }

  @Override
  @Test
  public void shouldGetResultNullFromResultSetByPosition() throws Exception {
    when(rs.getArray(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(rs, 1));
  }

  @Override
  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    when(cs.getArray(1)).thenReturn(mockArray);
    String[] stringArray = new String[]{"a", "b"};
    when(mockArray.getArray()).thenReturn(stringArray);
    assertEquals(stringArray, TYPE_HANDLER.getResult(cs, 1));
    verify(mockArray).free();
  }

  @Override
  @Test
  public void shouldGetResultNullFromCallableStatement() throws Exception {
    when(cs.getArray(1)).thenReturn(null);
    assertNull(TYPE_HANDLER.getResult(cs, 1));
  }

}
