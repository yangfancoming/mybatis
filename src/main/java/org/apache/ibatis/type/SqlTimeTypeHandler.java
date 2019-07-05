
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;


public class SqlTimeTypeHandler extends BaseTypeHandler<Time> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Time parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setTime(i, parameter);
  }

  @Override
  public Time getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getTime(columnName);
  }

  @Override
  public Time getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    return rs.getTime(columnIndex);
  }

  @Override
  public Time getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getTime(columnIndex);
  }
}
