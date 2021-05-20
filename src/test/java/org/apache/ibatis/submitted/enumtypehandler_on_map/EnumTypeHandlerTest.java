
package org.apache.ibatis.submitted.enumtypehandler_on_map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.enumtypehandler_on_map.Person.Type;
import org.apache.ibatis.submitted.enumtypehandler_on_map.PersonMapper.TypeName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

class EnumTypeHandlerTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    static void initDatabase() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/enumtypehandler_on_map/ibatisConfig.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }

        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
                "org/apache/ibatis/submitted/enumtypehandler_on_map/CreateDB.sql");
    }

    @Test
    void testEnumWithParam() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession() ) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            List<Person> persons = personMapper.getByType(Person.Type.PERSON, "");
            Assertions.assertNotNull(persons, "Persons must not be null");
            Assertions.assertEquals(1, persons.size(), "Persons must contain exactly 1 person");
        }
    }
    @Test
    void testEnumWithoutParam() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            List<Person> persons = personMapper.getByTypeNoParam(new TypeName() {
                @Override
                public String getName() {
                    return "";
                }

                @Override
                public Type getType() {
                    return Person.Type.PERSON;
                }
            });
            Assertions.assertNotNull(persons, "Persons must not be null");
            Assertions.assertEquals(1, persons.size(), "Persons must contain exactly 1 person");
        }
    }
}
