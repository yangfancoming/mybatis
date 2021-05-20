
package org.apache.ibatis.submitted.cglib_lazy_error;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

class CglibNPELazyTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void initDatabase() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/cglib_lazy_error/ibatisConfigLazy.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSessionFactory.getConfiguration().setLazyLoadingEnabled(true);
      sqlSessionFactory.getConfiguration().setAggressiveLazyLoading(false);
    }

    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/cglib_lazy_error/CreateDB.sql");
  }

  @Test
  void testNoParent() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
      Person person = personMapper.selectById(1);
      Assertions.assertNotNull(person, "Persons must not be null");
      Person parent = person.getParent();
      Assertions.assertNull(parent, "Parent must be null");
    }
  }

  @Test
  void testAncestorSelf() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
      Person person = personMapper.selectById(1);
      Assertions.assertNotNull(person, "Persons must not be null");
      Person ancestor = person.getAncestor();
      Assertions.assertEquals(person, ancestor, "Ancestor must be John Smith sr.");
    }
  }

  @Test
  void testAncestorAfterQueryingParents() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
      Person expectedAncestor = personMapper.selectById(1);
      Person person = personMapper.selectById(3);
      // Load ancestor indirectly.
      Assertions.assertNotNull(person, "Persons must not be null");
      Assertions.assertNotNull(person.getParent(), "Parent must not be null");
      Assertions.assertNotNull(person.getParent().getParent(), "Grandparent must not be null");
      Assertions.assertEquals(expectedAncestor, person.getAncestor(), "Ancestor must be John Smith sr.");
    }
  }

  @Test
  void testGrandParent() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
      Person expectedParent = personMapper.selectById(2);
      Person expectedGrandParent = personMapper.selectById(1);
      Person person = personMapper.selectById(3);
      Assertions.assertNotNull(person, "Persons must not be null");
      final Person actualParent = person.getParent();
      final Person actualGrandParent = person.getParent().getParent();
      Assertions.assertEquals(expectedParent, actualParent);
      Assertions.assertEquals(expectedGrandParent, actualGrandParent);
    }
  }

  @Test
  void testAncestor() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
      Person expectedAncestor = personMapper.selectById(1);
      Person person = personMapper.selectById(3);
      Assertions.assertNotNull(person, "Persons must not be null");
      final Person actualAncestor = person.getAncestor();
      Assertions.assertEquals(expectedAncestor, actualAncestor);
    }
  }

}
