
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericTypeSupportedInHierarchiesTestCase {

  @Test
  void detectsTheGenericTypeTraversingTheHierarchy() {
    assertEquals(String.class, new CustomStringTypeHandler().getRawType());
  }

  /**
   *
   */
  public static final class CustomStringTypeHandler extends StringTypeHandler {

    /**
     * Defined as reported in #581
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
      // do something
      super.setNonNullParameter(ps, i, parameter, jdbcType);
    }

  }

}
