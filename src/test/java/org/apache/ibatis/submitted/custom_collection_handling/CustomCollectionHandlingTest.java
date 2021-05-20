
package org.apache.ibatis.submitted.custom_collection_handling;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomCollectionHandlingTest {

  String xmlConfig = "org/apache/ibatis/submitted/custom_collection_handling/MapperConfig.xml";

  /**
     * Custom collections with nested resultMap.
     */
    @Test
    void testSelectListWithNestedResultMap() throws Exception {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.custom_collection_handling.PersonMapper.findWithResultMap");
            assertEquals(2, list.size());
            assertEquals(2, list.get(0).getContacts().size());
            assertEquals(1, list.get(1).getContacts().size());
            assertEquals("3 Wall Street", list.get(0).getContacts().get(1).getAddress());
        }
    }

    /**
     * Custom collections with nested select.
     */
    @Test
    void testSelectListWithNestedSelect() throws Exception {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.custom_collection_handling.PersonMapper.findWithSelect");
            assertEquals(2, list.size());
            assertEquals(2, list.get(0).getContacts().size());
            assertEquals(1, list.get(1).getContacts().size());
            assertEquals("3 Wall Street", list.get(0).getContacts().get(1).getAddress());
        }
    }

    private SqlSessionFactory getSqlSessionFactoryXmlConfig(String resource) throws Exception {
        try (Reader configReader = Resources.getResourceAsReader(resource)) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);

            initDb(sqlSessionFactory);

            return sqlSessionFactory;
        }
    }

    private static void initDb(SqlSessionFactory sqlSessionFactory) throws IOException, SQLException {
        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/custom_collection_handling/CreateDB.sql");
    }
}
