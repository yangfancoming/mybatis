
package org.apache.ibatis.submitted.default_method;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.default_method.Mapper.SubMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DefaultMethodTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader(
        "org/apache/ibatis/submitted/default_method/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
        "org/apache/ibatis/submitted/default_method/CreateDB.sql");
  }

  @Test
  void shouldInvokeDefaultMethod() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.defaultGetUser(1);
      assertEquals("User1", user.getName());
    }
  }

  @Test
  void shouldInvokeDefaultMethodOfSubclass() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      SubMapper mapper = sqlSession.getMapper(SubMapper.class);
      User user = mapper.defaultGetUser("User1", 1);
      assertEquals("User1", user.getName());
    }
  }

  @Test
  void shouldInvokeDefaultMethodOfPackagePrivateMapper() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PackageMapper mapper = sqlSession.getMapper(PackageMapper.class);
      User user = mapper.defaultGetUser(1);
      assertEquals("User1", user.getName());
    }
  }
}
