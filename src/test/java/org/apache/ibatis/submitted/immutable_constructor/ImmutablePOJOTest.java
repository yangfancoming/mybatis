
package org.apache.ibatis.submitted.immutable_constructor;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ImmutablePOJOTest {

  private static final Integer POJO_ID = 1;
  private static final String POJO_DESCRIPTION = "Description of immutable";

  private static SqlSessionFactory factory;

  @BeforeAll
  static void setupClass() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/immutable_constructor/ibatisConfig.xml")) {
      factory = new SqlSessionFactoryBuilder().build(reader);
    }

    BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/immutable_constructor/CreateDB.sql");
  }

  @Test
  void shouldLoadImmutablePOJOBySignature() {
    try (SqlSession session = factory.openSession()) {
      final ImmutablePOJOMapper mapper = session.getMapper(ImmutablePOJOMapper.class);
      final ImmutablePOJO pojo = mapper.getImmutablePOJO(POJO_ID);

      assertEquals(POJO_ID, pojo.getImmutableId());
      assertEquals(POJO_DESCRIPTION, pojo.getImmutableDescription());
    }
  }

  @Test
  void shouldFailLoadingImmutablePOJO() {
    try (SqlSession session = factory.openSession()) {
      final ImmutablePOJOMapper mapper = session.getMapper(ImmutablePOJOMapper.class);
      Assertions.assertThrows(PersistenceException.class, () -> mapper.getImmutablePOJONoMatchingConstructor(POJO_ID));
    }
  }

}
