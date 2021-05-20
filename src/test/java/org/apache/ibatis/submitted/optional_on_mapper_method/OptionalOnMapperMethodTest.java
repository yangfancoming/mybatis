
package org.apache.ibatis.submitted.optional_on_mapper_method;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.Reader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

/**
 * Tests for support the {@code java.util.Optional} as return type of mapper method.
 * @since 3.5.0
 */
class OptionalOnMapperMethodTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/optional_on_mapper_method/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/optional_on_mapper_method/CreateDB.sql");
  }

  @Test
  void returnNotNullOnAnnotation() {
    Optional<User> user = mapper.getUserUsingAnnotation(1);
    assertTrue(user.isPresent());
    assertEquals("User1", user.get().getName());
  }

  @Test
  void returnNullOnAnnotation() {
    Optional<User> user = mapper.getUserUsingAnnotation(3);
    assertFalse(user.isPresent());
  }

  @Test
  void returnNotNullOnXml() {
    Optional<User> user = mapper.getUserUsingXml(2);
    assertTrue(user.isPresent());
    assertEquals("User2", user.get().getName());
  }

  @Test
  void returnNullOnXml() {
    Optional<User> user = mapper.getUserUsingXml(3);
    assertFalse(user.isPresent());
  }

  @Test
  void returnOptionalFromSqlSession() {
    try (SqlSession sqlSession = Mockito.spy(sqlSessionFactory.openSession())) {
      User mockUser = new User();
      mockUser.setName("mock user");
      Optional<User> optionalMockUser = Optional.of(mockUser);
      doReturn(optionalMockUser).when(sqlSession).selectOne(any(String.class), any(Object.class));

      Mapper mapper = sqlSession.getMapper(Mapper.class);
      Optional<User> user = mapper.getUserUsingAnnotation(3);
      assertSame(optionalMockUser, user);
    }
  }
}
