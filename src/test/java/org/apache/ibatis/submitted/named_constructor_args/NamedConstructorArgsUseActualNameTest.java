
package org.apache.ibatis.submitted.named_constructor_args;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NamedConstructorArgsUseActualNameTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/named_constructor_args/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    sqlSessionFactory.getConfiguration().addMapper(UseActualNameMapper.class);
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/named_constructor_args/CreateDB.sql");
  }

  // doit ？ 测试用例为啥报错？？？
  @Test
  void argsByActualNames() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      UseActualNameMapper mapper = sqlSession.getMapper(UseActualNameMapper.class);
      User user = mapper.mapConstructorWithoutParamAnnos(1);
      assertEquals(Integer.valueOf(1), user.getId());
      assertEquals("User1", user.getName());
    }
  }

  @Test
  void argsByActualNamesXml() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      UseActualNameMapper mapper = sqlSession.getMapper(UseActualNameMapper.class);
      User user = mapper.mapConstructorWithoutParamAnnosXml(1);
      assertEquals(Integer.valueOf(1), user.getId());
      assertEquals("User1", user.getName());
    }
  }
}
