
package org.apache.ibatis.submitted.sql;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.jdbc.SQL;

public interface Mapper {

  @SelectProvider(type = SqlProvider.class)
  List<User> findAll(@Param("offset") long offset, @Param("limit") int limit);

  class SqlProvider implements ProviderMethodResolver {
    public String findAll() {
      return new SQL()
        .SELECT("user_id", "name")
        .FROM("${schema}users")
        .ORDER_BY("user_id")
        .OFFSET_ROWS("#{offset}")
        .FETCH_FIRST_ROWS_ONLY("#{limit}")
        .toString();
    }
  }

}
