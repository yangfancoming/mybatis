
package org.apache.ibatis.submitted.ognl.ognl_enum;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.ognl.ognl_enum.Person.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

class EnumWithOgnlTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static PersonMapper personMapper;

  List<Person> persons;
  @BeforeAll
  static void initDatabase() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/ognl/ognl_enum/ibatisConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      personMapper = sqlSession.getMapper(PersonMapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/ibatis/submitted/ognl/ognl_enum/CreateDB.sql");
  }

  @Test
  void testEnumWithOgnl() {
     persons = personMapper.selectAllByType(null);
    Assertions.assertEquals(3, persons.size(), "Persons must contain 3 persons");
  }

  @Test
  void testEnumWithOgnlDirector() {
     persons = personMapper.selectAllByType(Person.Type.DIRECTOR);
  }

  @Test
  void testEnumWithOgnlDirectorNameAttribute() {
     persons = personMapper.selectAllByTypeNameAttribute(Person.Type.DIRECTOR);
  }

  @Test
  void testEnumWithOgnlDirectorWithInterface() {
     persons = personMapper.selectAllByTypeWithInterface(() -> Type.DIRECTOR);
  }

  @Test
  void testEnumWithOgnlDirectorNameAttributeWithInterface() {
     persons = personMapper.selectAllByTypeNameAttributeWithInterface(() -> Type.DIRECTOR);
  }

  @AfterEach
  public void after(){
    Assertions.assertEquals(1, persons.size(), "Persons must contain 1 persons");
  }
}
