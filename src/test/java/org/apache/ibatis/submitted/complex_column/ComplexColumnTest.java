
package org.apache.ibatis.submitted.complex_column;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

class ComplexColumnTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static PersonMapper personMapper;

  @BeforeAll
  static void initDatabase() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/complex_column/ibatisConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      personMapper = sqlSession.getMapper(PersonMapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/complex_column/CreateDB.sql");
  }

  @Test
  void testWithoutComplex() {
      Person person = personMapper.getWithoutComplex(2L);
      Assertions.assertNotNull(person, "person must not be null");
      Assertions.assertEquals("Christian", person.getFirstName());
      Assertions.assertEquals("Poitras", person.getLastName());
      Person parent = person.getParent();
      Assertions.assertNotNull(parent, "parent must not be null");
      Assertions.assertEquals("John", parent.getFirstName());
      Assertions.assertEquals("Smith", parent.getLastName());
  }

  @Test
  void testWithComplex() {
      Person person = personMapper.getWithComplex(2L);
      Assertions.assertNotNull(person, "person must not be null");
      Assertions.assertEquals("Christian", person.getFirstName());
      Assertions.assertEquals("Poitras", person.getLastName());
      Person parent = person.getParent();
      Assertions.assertNotNull(parent, "parent must not be null");
      Assertions.assertEquals("John", parent.getFirstName());
      Assertions.assertEquals("Smith", parent.getLastName());
  }

  @Test
  void testWithComplex2() {
      Person person = personMapper.getWithComplex2(2L);
      Assertions.assertNotNull(person, "person must not be null");
      Assertions.assertEquals("Christian", person.getFirstName());
      Assertions.assertEquals("Poitras", person.getLastName());
      Person parent = person.getParent();
      Assertions.assertNotNull(parent, "parent must not be null");
      Assertions.assertEquals("John", parent.getFirstName());
      Assertions.assertEquals("Smith", parent.getLastName());
  }

  @Test
  void testWithComplex3() {
      Person person = personMapper.getWithComplex3(2L);
      Assertions.assertNotNull(person, "person must not be null");
      Assertions.assertEquals("Christian", person.getFirstName());
      Assertions.assertEquals("Poitras", person.getLastName());
      Person parent = person.getParent();
      Assertions.assertNotNull(parent, "parent must not be null");
      Assertions.assertEquals("John", parent.getFirstName());
      Assertions.assertEquals("Smith", parent.getLastName());
  }

  @Test
  void testWithComplex4() {
      Person criteria = new Person();
      criteria.setFirstName("Christian");
      criteria.setLastName("Poitras");
      Person person = personMapper.getParentWithComplex(criteria);
      Assertions.assertNotNull(person, "person must not be null");
      Assertions.assertEquals("Christian", person.getFirstName());
      Assertions.assertEquals("Poitras", person.getLastName());
      Person parent = person.getParent();
      Assertions.assertNotNull(parent, "parent must not be null");
      Assertions.assertEquals("John", parent.getFirstName());
      Assertions.assertEquals("Smith", parent.getLastName());
  }

  @Test
  void testWithParamAttributes() {
      Person person = personMapper.getComplexWithParamAttributes(2L);
      Assertions.assertNotNull(person, "person must not be null");
      Assertions.assertEquals("Christian", person.getFirstName());
      Assertions.assertEquals("Poitras", person.getLastName());
      Person parent = person.getParent();
      Assertions.assertNotNull(parent, "parent must not be null");
      Assertions.assertEquals("John", parent.getFirstName());
      Assertions.assertEquals("Smith", parent.getLastName());
  }
}
