package org.apache.goat.chapter100.A.A025.item01;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 1. 实现TypeHandler接口,或者继承BaseTypeHandler
 */
public class MyEnumEmpStatusTypeHandler implements TypeHandler<SexEnum> {

  /**
   * 定义当前数据如何保存到数据库中
   */
  @Override
  public void setParameter(PreparedStatement ps, int i, SexEnum parameter, JdbcType jdbcType) throws SQLException {
    ps.setInt(i, parameter.getId());
  }

  @Override
  public SexEnum getResult(ResultSet rs, String columnName) throws SQLException {
    // 需要根据从数据库中拿到的枚举类型的状态码返回一个枚举对象
    System.out.println("从数据库中获取的状态码:" + columnName);
    return SexEnum.valueOf(columnName);
  }

  @Override
  public SexEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
    String code = rs.getString(columnIndex);
    System.out.println("从数据库中获取的状态码:" + code);
    return SexEnum.valueOf(code);
  }

  @Override
  public SexEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
    String code = cs.getString(columnIndex);
    System.out.println("从数据库中获取的状态码:" + code);
    return SexEnum.valueOf(code);
  }
}
