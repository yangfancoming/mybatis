
package org.apache.ibatis.jdbc;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class NullTest {

  @Test
  void shouldGetTypeAndTypeHandlerForNullStringType() {
    assertEquals(JdbcType.VARCHAR, Null.STRING.getJdbcType());
    assertTrue(Null.STRING.getTypeHandler() instanceof StringTypeHandler);
  }

}
