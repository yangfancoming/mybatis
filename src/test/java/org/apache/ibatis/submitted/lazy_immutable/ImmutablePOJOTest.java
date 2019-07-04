
package org.apache.ibatis.submitted.lazy_immutable;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;

import java.io.Reader;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ImmutablePOJOTest {

    private static final Integer POJO_ID = 1;
    private static SqlSessionFactory factory;

    @BeforeAll
    static void setupClass() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/lazy_immutable/ibatisConfig.xml")) {
            factory = new SqlSessionFactoryBuilder().build(reader);
        }

        BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),
                "org/apache/ibatis/submitted/lazy_immutable/CreateDB.sql");
    }

    @Test
    void testLoadLazyImmutablePOJO() {
        try (SqlSession session = factory.openSession()) {
            final ImmutablePOJOMapper mapper = session.getMapper(ImmutablePOJOMapper.class);
            final ImmutablePOJO pojo = mapper.getImmutablePOJO(POJO_ID);

            assertEquals(POJO_ID, pojo.getId());
            assertNotNull(pojo.getDescription(), "Description should not be null.");
          assertNotEquals(0, pojo.getDescription().length(), "Description should not be empty.");
        }
    }

}
