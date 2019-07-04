
package org.apache.ibatis.submitted.dynsql2;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;

public class LastNameTypeHandler implements TypeHandler {

  @Override
  public Object getResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getString(columnIndex);
  }

  @Override
  public Object getResult(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getString(columnName);
  }

  @Override
  public Object getResult(ResultSet rs, int columnIndex)
      throws SQLException {
    return rs.getString(columnIndex);
  }

  @Override
  public void setParameter(PreparedStatement ps, int i, Object parameter,
                           JdbcType jdbcType) throws SQLException {
    if (parameter == null) {
      ps.setNull(i, Types.VARCHAR);
    } else {
      Name name = (Name) parameter;
      ps.setString(i, name.getLastName());
    }
  }
}
