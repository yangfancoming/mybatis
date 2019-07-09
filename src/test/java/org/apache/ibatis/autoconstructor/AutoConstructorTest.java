
package org.apache.ibatis.autoconstructor;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutoConstructorTest {

  private static SqlSessionFactory sqlSessionFactory;
  public static  AutoConstructorMapper mapper ;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/autoconstructor/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    SqlSession sqlSession = sqlSessionFactory.openSession();
     mapper = sqlSession.getMapper(AutoConstructorMapper.class);
    // populate in-memory database 填充内存数据库
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/autoconstructor/CreateDB.sql");

  }

  @Test
  void fullyPopulatedSubject() {
      final Object subject = mapper.getSubject(1);
      assertNotNull(subject);
  }

  @Test
  void primitiveSubjects() {
    List<PrimitiveSubject> subjects = mapper.getSubjects();
    System.out.println(subjects);
//    assertThrows(PersistenceException.class, mapper::getSubjects);
  }

  @Test
  void annotatedSubject() {
      verifySubjects(mapper.getAnnotatedSubjects());
  }

  @Test
  void badSubject() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
      assertThrows(PersistenceException.class, mapper::getBadSubjects);
    }
  }

  @Test
  void extensiveSubject() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final AutoConstructorMapper mapper = sqlSession.getMapper(AutoConstructorMapper.class);
      verifySubjects(mapper.getExtensiveSubject());
    }
  }

  private void verifySubjects(final List<?> subjects) {
    assertNotNull(subjects);
    Assertions.assertThat(subjects.size()).isEqualTo(3);
  }
}
