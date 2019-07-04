
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

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
