
package org.apache.ibatis.submitted.typehandlerinjection;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TypeHandlerInjectionTest {

  private static SqlSessionFactory sqlSessionFactory;

  private static UserStateTypeHandler<String> handler = new UserStateTypeHandler<>();

  @BeforeAll
  static void setUp() throws Exception {
    // create a SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/typehandlerinjection/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(handler);
    sqlSessionFactory.getConfiguration().addMapper(Mapper.class);

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/typehandlerinjection/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getUsers();
      Assertions.assertEquals("Inactive", users.get(0).getName());
    }
  }

}
