
package org.apache.ibatis.submitted.typehandlerinjection;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UserStateTypeHandler<E> implements TypeHandler<Object> {

  private static Map<String, String> lookup;

  static {
    lookup = new HashMap<>();
    lookup.put("0", "INACTIVE");
    lookup.put("1", "ACTIVE");
  }

  UserStateTypeHandler() {
    // can only be constructed from this package
  }

  @Override
  public Object getResult(ResultSet rs, String arg) throws SQLException {
    return lookupValue(rs.getInt(arg));
  }

  @Override
  public Object getResult(ResultSet rs, int arg) throws SQLException {
    return lookupValue(rs.getInt(arg));
  }

  @Override
  public Object getResult(CallableStatement cs, int arg) throws SQLException {
    return lookupValue(cs.getInt(arg));
  }

  @Override
  public void setParameter(PreparedStatement ps, int i, Object value, JdbcType jdbcType) throws SQLException {

    String key = "";
    for (Entry<String, String> entry : lookup.entrySet()) {
      if (value.equals(entry.getValue())) {
        key = entry.getKey();
      }
    }
    ps.setInt(i, Integer.valueOf(key));
  }

  private String lookupValue(int val) {
    return lookup.get(String.valueOf(val));
  }
}
