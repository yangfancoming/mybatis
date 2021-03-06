
package org.apache.ibatis.submitted.rounding;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;

class RoundingHandlersTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {

    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/rounding/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }


    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/rounding/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession session = sqlSessionFactory.openSession()) {
      Mapper mapper = session.getMapper(Mapper.class);
      User user = mapper.getUser(1);
      Assertions.assertEquals("User1", user.getName());
      Assertions.assertEquals(RoundingMode.UP, user.getRoundingMode());
      user = mapper.getUser2(1);
      Assertions.assertEquals("User1", user.getName());
      Assertions.assertEquals(RoundingMode.UP, user.getRoundingMode());
    }
  }

  @Test
  void shouldInsertUser2() {
    try (SqlSession session = sqlSessionFactory.openSession()) {
      Mapper mapper = session.getMapper(Mapper.class);
      User user = new User();
      user.setId(2);
      user.setName("User2");
      user.setFunkyNumber(BigDecimal.ZERO);
      user.setRoundingMode(RoundingMode.UNNECESSARY);
      mapper.insert(user);
      mapper.insert2(user);
    }
  }

}
