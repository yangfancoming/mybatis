
package org.apache.ibatis.submitted.enum_with_method;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnumWithMethodTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/enum_with_method/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/enum_with_method/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    User user = mapper.getUser(1);
    Assertions.assertEquals("User1", user.getName());
  }

  @Test
  void shouldInsertAUser() {
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    User user = new User();
    user.setId(2);
    user.setName("User2");
    user.setCur(Currency.Dollar);
    mapper.insertUser(user);
  }

}
