
package org.apache.ibatis.submitted.complex_property;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.Calendar;

class ComponentTest {
  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setup() throws Exception {
    String resource = "org/apache/ibatis/submitted/complex_property/Configuration.xml";
    Reader reader = Resources.getResourceAsReader(resource);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/complex_property/db.sql");
  }

  @Test
  void shouldInsertNestedPasswordFieldOfComplexType() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      //Create User
      User user = new User();
      user.setId(500000L);
      user.setPassword(new EncryptedString("secret"));
      user.setUsername("johnny" + Calendar.getInstance().getTimeInMillis());// random
      user.setAdministrator(true);
      sqlSession.insert("User.insert", user);
      // Retrieve User
      user = sqlSession.selectOne("User.find", user.getId());
      assertNotNull(user.getId());
      sqlSession.rollback();
    }
  }

}
