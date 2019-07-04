
package org.apache.ibatis.submitted.substitution_in_annots;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SubstitutionInAnnotsTest {

  protected static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    Configuration configuration = new Configuration();
    Environment environment = new Environment("test", new JdbcTransactionFactory(), new UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:annots", null));
    configuration.setEnvironment(environment);
    configuration.addMapper(SubstitutionInAnnotsMapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/substitution_in_annots/CreateDB.sql");
  }

  @Test
  void testSubstitutionWithXml() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithXml(4));
    }
  }

  @Test
  void testSubstitutionWithAnnotsValue() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsValue(4));
    }
  }

  @Test
  void testSubstitutionWithAnnotsParameter() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParameter(4));
    }
  }

  @Test
  void testSubstitutionWithAnnotsParamAnnot() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParamAnnot(4));
    }
  }

}
