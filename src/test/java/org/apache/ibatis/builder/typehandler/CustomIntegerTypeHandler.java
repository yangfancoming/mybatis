
package org.apache.ibatis.builder.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Integer.class)
public class CustomIntegerTypeHandler implements TypeHandler<Integer> {

  @Override
  public void setParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
    ps.setInt(i, parameter);
  }

  @Override
  public Integer getResult(ResultSet rs, String columnName) throws SQLException {
    return rs.getInt(columnName);
  }

  @Override
  public Integer getResult(ResultSet rs, int columnIndex) throws SQLException {
    return rs.getInt(columnIndex);
  }

  @Override
  public Integer getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return cs.getInt(columnIndex);
  }

}
