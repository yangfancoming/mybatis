
package org.apache.ibatis.submitted.flush_statement_npe;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FlushStatementNpeTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    static void initDatabase() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/flush_statement_npe/ibatisConfig.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/flush_statement_npe/CreateDB.sql");
    }

    @Test
    void testSameUpdateAfterCommitSimple() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE)) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectById(1);
            person.setFirstName("Simone");
            // Execute first update then commit.
            personMapper.update(person);
            sqlSession.commit();
            // Execute same update a second time. This used to raise an NPE.
            personMapper.update(person);
            sqlSession.commit();
        }
    }
    @Test
    void testSameUpdateAfterCommitReuse() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE)) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectById(1);
            person.setFirstName("Simone");
            // Execute first update then commit.
            personMapper.update(person);
            sqlSession.commit();
            // Execute same update a second time. This used to raise an NPE.
            personMapper.update(person);
            sqlSession.commit();
        }
    }
    @Test
    void testSameUpdateAfterCommitBatch() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectById(1);
            person.setFirstName("Simone");
            // Execute first update then commit.
            personMapper.update(person);
            sqlSession.commit();
            // Execute same update a second time. This used to raise an NPE.
            personMapper.update(person);
            sqlSession.commit();
        }
    }
}
