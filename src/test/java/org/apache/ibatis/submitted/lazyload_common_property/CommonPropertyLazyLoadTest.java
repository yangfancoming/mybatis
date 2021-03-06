
package org.apache.ibatis.submitted.lazyload_common_property;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

class CommonPropertyLazyLoadTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    static void initDatabase() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/lazyload_common_property/ibatisConfig.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/lazyload_common_property/CreateDB.sql");
    }

    @Test
    void testLazyLoadWithNoAncestor() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            childMapper.selectById(1);
        }
    }
    @Test
    void testLazyLoadWithFirstAncestor() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            fatherMapper.selectById(1);
            childMapper.selectById(1);
        }
    }
    @Test
    void testLazyLoadWithAllAncestors() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            GrandFatherMapper grandFatherMapper = sqlSession.getMapper(GrandFatherMapper.class);
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            grandFatherMapper.selectById(1);
            fatherMapper.selectById(1);
            childMapper.selectById(1);
        }
    }
    @Test
    void testLazyLoadSkipFirstAncestor() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            GrandFatherMapper grandFatherMapper = sqlSession.getMapper(GrandFatherMapper.class);
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            grandFatherMapper.selectById(1);
            childMapper.selectById(1);
        }
    }
}
