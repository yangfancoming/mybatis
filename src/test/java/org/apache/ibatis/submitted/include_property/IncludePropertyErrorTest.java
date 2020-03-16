
package org.apache.ibatis.submitted.include_property;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IncludePropertyErrorTest {

  @Test
  void shouldFailForDuplicatedPropertyNames() {
    Configuration configuration = new Configuration();
    Assertions.assertThrows(PersistenceException.class,() -> configuration.addMapper(DuplicatedIncludePropertiesMapper.class));
  }

}
