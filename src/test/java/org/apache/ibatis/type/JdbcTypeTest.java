
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Types;

import org.junit.jupiter.api.Test;

class JdbcTypeTest {
  private static final String[] requiredStandardTypeNames = {
    "ARRAY", "BIGINT", "BINARY", "BIT", "BLOB", "BOOLEAN", "CHAR", "CLOB",
    "DATALINK", "DATE", "DECIMAL", "DISTINCT", "DOUBLE", "FLOAT", "INTEGER",
    "JAVA_OBJECT", "LONGNVARCHAR", "LONGVARBINARY", "LONGVARCHAR", "NCHAR",
    "NCLOB", "NULL", "NUMERIC","NVARCHAR", "OTHER", "REAL", "REF", "ROWID",
    "SMALLINT", "SQLXML", "STRUCT", "TIME", "TIMESTAMP", "TINYINT",
    "VARBINARY", "VARCHAR"
  };

  @Test
  void shouldHaveRequiredStandardConstants() throws Exception {
    for (String typeName : requiredStandardTypeNames) {
      int typeCode = Types.class.getField(typeName).getInt(null);
      JdbcType jdbcType = JdbcType.valueOf(typeName);
      assertEquals(typeCode, jdbcType.TYPE_CODE);
    }
  }

  @Test
  void shouldHaveDateTimeOffsetConstant() {
    JdbcType jdbcType = JdbcType.valueOf("DATETIMEOFFSET");
    assertEquals(-155, jdbcType.TYPE_CODE);
  }
}
