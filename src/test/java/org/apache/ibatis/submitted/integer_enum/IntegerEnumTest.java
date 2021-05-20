
package org.apache.ibatis.submitted.integer_enum;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.Reader;

class IntegerEnumTest extends BaseDataTest {

  @Test
  void shouldParseMapWithIntegerJdbcType() throws Exception {
    String resource = "org/apache/ibatis/submitted/integer_enum/MapperConfig.xml";
    Reader reader = Resources.getResourceAsReader(resource);
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    builder.build(reader);
  }
}
