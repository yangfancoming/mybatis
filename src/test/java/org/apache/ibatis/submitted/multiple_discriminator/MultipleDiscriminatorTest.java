
package org.apache.ibatis.submitted.multiple_discriminator;

import java.io.Reader;
import java.time.Duration;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MultipleDiscriminatorTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    static void initDatabase() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multiple_discriminator/ibatisConfig.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }

        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
                "org/apache/ibatis/submitted/multiple_discriminator/CreateDB.sql");
    }

    @Test
    void testMultipleDiscriminator() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.get(1L);
            Assertions.assertNotNull(person, "Person must not be null");
            Assertions.assertEquals(Director.class, person.getClass(), "Person must be a director");
        }
    }
    @Test
    void testMultipleDiscriminator2() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.get2(1L);
            Assertions.assertNotNull(person, "Person must not be null");
            Assertions.assertEquals(Director.class, person.getClass(), "Person must be a director");
        }
    }
    @Test
    void testMultipleDiscriminatorLoop() {
        Assertions.assertTimeout(Duration.ofMillis(20), () -> {
          try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            personMapper.getLoop();
          }
        });
    }
}
