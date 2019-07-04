
package org.apache.ibatis.binding;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WrongNamespacesTest {

  @Test
  void shouldFailForWrongNamespace() {
    Configuration configuration = new Configuration();
    Assertions.assertThrows(RuntimeException.class, () -> configuration.addMapper(WrongNamespaceMapper.class));
  }

  @Test
  void shouldFailForMissingNamespace() {
    Configuration configuration = new Configuration();
    Assertions.assertThrows(RuntimeException.class, () -> configuration.addMapper(MissingNamespaceMapper.class));
  }

}
