
package org.apache.ibatis.submitted.unknownobject;

import java.io.Reader;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnknownObjectTest {

  @Test
  void shouldFailBecauseThereIsAPropertyWithoutTypeHandler() throws Exception {
    // create a SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/unknownobject/mybatis-config.xml")) {
      Assertions.assertThrows(PersistenceException.class, () -> new SqlSessionFactoryBuilder().build(reader));
    }
  }

}
