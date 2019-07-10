
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
  private static SqlSession sqlSession;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/default_method/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/default_method/CreateDB.sql");
  }

  @Test
  void shouldInvokeDefaultMethod() {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.defaultGetUser(1);
      assertEquals("User1", user.getName());
  }

  @Test
  void shouldInvokeDefaultMethodOfSubclass() {
      SubMapper mapper = sqlSession.getMapper(SubMapper.class);
      User user = mapper.defaultGetUser("User1", 1);
      assertEquals("User1", user.getName());
  }

  @Test
  void shouldInvokeDefaultMethodOfPackagePrivateMapper() {
      PackageMapper mapper = sqlSession.getMapper(PackageMapper.class);
      User user = mapper.defaultGetUser(1);
      assertEquals("User1", user.getName());
  }
}
