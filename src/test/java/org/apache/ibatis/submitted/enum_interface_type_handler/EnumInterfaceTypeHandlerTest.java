
package org.apache.ibatis.submitted.enum_interface_type_handler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnumInterfaceTypeHandlerTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader(
        "org/apache/ibatis/submitted/enum_interface_type_handler/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/enum_interface_type_handler/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUser(1);
      assertEquals(Color.RED, user.getColor());
    }
  }

  @Test
  void shouldInsertAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = new User();
      user.setId(2);
      user.setColor(Color.BLUE);
      mapper.insertUser(user);
      User result = mapper.getUser(2);
      assertEquals(Color.BLUE, result.getColor());
    }
  }

  @Test
  void shouldInsertAUserWithoutParameterTypeInXmlElement() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      XmlMapper mapper = sqlSession.getMapper(XmlMapper.class);
      User user = new User();
      user.setId(2);
      user.setColor(Color.BLUE);
      mapper.insertUser(user);
      User result = sqlSession.getMapper(Mapper.class).getUser(2);
      assertEquals(Color.BLUE, result.getColor());
    }
  }

}
