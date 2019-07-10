
package org.apache.ibatis.submitted.named_constructor_args;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NamedConstructorArgsTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/named_constructor_args/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    Configuration configuration = sqlSessionFactory.getConfiguration();
    configuration.setUseActualParamName(false);
    configuration.addMapper(Mapper.class);


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/named_constructor_args/CreateDB.sql");
  }

  @Test
  void argsWithParamAnnos() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.mapConstructorWithParamAnnos(1);
      assertEquals(Integer.valueOf(1), user.getId());
      assertEquals("User1", user.getName());
      assertEquals(Long.valueOf(99L), user.getTeam());
    }
  }

  @Test
  void argsWithParamAnnosXml() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.mapConstructorWithParamAnnosXml(1);
      assertEquals(Integer.valueOf(1), user.getId());
      assertEquals("User1", user.getName());
      assertEquals(Long.valueOf(99L), user.getTeam());
    }
  }

}
