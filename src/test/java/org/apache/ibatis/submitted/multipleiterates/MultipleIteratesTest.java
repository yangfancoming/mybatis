
package org.apache.ibatis.submitted.multipleiterates;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MultipleIteratesTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {

    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multipleiterates/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/multipleiterates/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = new User();
      user.setId(1);
      user.setName("Justin");
      user.setFirstAttr(new String[] { "asd", "asd" });
      user.setSecondAttr(new String[] { "fds", "fds" });
      mapper.insertUser(user);
    }
  }

}
