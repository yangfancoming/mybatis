
package org.apache.ibatis.binding;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WrongMapperTest {

  @Test
  void shouldFailForBothOneAndMany() {
    Configuration configuration = new Configuration();
    Assertions.assertThrows(RuntimeException.class, () -> configuration.addMapper(MapperWithOneAndMany.class));
  }

}
