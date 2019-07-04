

package org.apache.ibatis.submitted.hashmaptypehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class HashMapTypeHandler extends BaseTypeHandler<HashMap<String, String>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, HashMap<String, String> parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, parameter.get("name"));
  }

  @Override
  public HashMap<String, String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return null;
  }

  @Override
  public HashMap<String, String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return null;
  }

  @Override
  public HashMap<String, String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return null;
  }
}
