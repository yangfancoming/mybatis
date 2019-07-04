
package org.apache.ibatis.submitted.xml_external_ref;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

class MultipleReverseIncludeTest {

  @Test
  void testMultipleReverseIncludeXmlConfig() throws Exception {
    testMultipleReverseIncludes(getSqlSessionFactoryXmlConfig());
  }

  @Test
  void testMultipleReverseIncludeJavaConfig() throws Exception {
    testMultipleReverseIncludes(getSqlSessionFactoryJavaConfig());
  }

  private void testMultipleReverseIncludes(SqlSessionFactory sqlSessionFactory) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MultipleReverseIncludePersonMapper personMapper = sqlSession.getMapper(MultipleReverseIncludePersonMapper.class);
      Person person = personMapper.select(1);
      assertEquals((Integer) 1, person.getId());
      assertEquals("John", person.getName());
    }
  }

  private SqlSessionFactory getSqlSessionFactoryXmlConfig() throws Exception {
    try (Reader configReader = Resources
        .getResourceAsReader("org/apache/ibatis/submitted/xml_external_ref/MultipleReverseIncludeMapperConfig.xml")) {
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);

      initDb(sqlSessionFactory);

      return sqlSessionFactory;
    }
  }

  private SqlSessionFactory getSqlSessionFactoryJavaConfig() throws Exception {
    Configuration configuration = new Configuration();
    Environment environment = new Environment("development", new JdbcTransactionFactory(), new UnpooledDataSource(
        "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:xmlextref", null));
    configuration.setEnvironment(environment);
    configuration.addMapper(MultipleReverseIncludePersonMapper.class);

    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    initDb(sqlSessionFactory);

    return sqlSessionFactory;
  }

  private static void initDb(SqlSessionFactory sqlSessionFactory) throws IOException, SQLException {
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/xml_external_ref/CreateDB.sql");
  }

}
