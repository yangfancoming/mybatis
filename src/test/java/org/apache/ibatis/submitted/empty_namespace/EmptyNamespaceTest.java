
package org.apache.ibatis.submitted.empty_namespace;

import java.io.Reader;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EmptyNamespaceTest {
  @Test
  void testEmptyNamespace() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/empty_namespace/ibatisConfig.xml")) {
        Assertions.assertThrows(PersistenceException.class, () -> new SqlSessionFactoryBuilder().build(reader));
    }
  }
}
