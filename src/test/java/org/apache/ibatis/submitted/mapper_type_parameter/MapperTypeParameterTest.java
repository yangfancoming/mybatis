
package org.apache.ibatis.submitted.mapper_type_parameter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MapperTypeParameterTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/mapper_type_parameter/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/mapper_type_parameter/CreateDB.sql");
  }

  @Test
  void shouldResolveReturnType() {
    PersonMapper mapper = sqlSession.getMapper(PersonMapper.class);
    Person person = mapper.select(new Person(1));
    assertEquals("Jane", person.getName());
  }

  @Test
  void shouldResolveListTypeParam() {
    PersonMapper mapper = sqlSession.getMapper(PersonMapper.class);
    List<Person> persons = mapper.selectList(null);
    assertEquals(2, persons.size());
    assertEquals("Jane", persons.get(0).getName());
    assertEquals("John", persons.get(1).getName());
  }

  @Test
  void shouldResolveMultipleTypeParam() {
    CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
    Map<Long, Country> results = mapper.selectMap(new Country());
    assertEquals(2, results.size());
    assertEquals("Japan", results.get(1L).getName());
    assertEquals("New Zealand", results.get(2L).getName());
  }

  @Test
  void shouldResolveParameterizedReturnType() {
    PersonListMapper mapper = sqlSession.getMapper(PersonListMapper.class);
    List<Person> persons = mapper.select(null);
    assertEquals(2, persons.size());
    assertEquals("Jane", persons.get(0).getName());
    assertEquals("John", persons.get(1).getName());
  }

  @Test
  void shouldResolveParam() {
    CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
    assertEquals(1, mapper.update(new Country(2L, "Greenland")));
  }

  @Test
  void shouldResolveListParam() {
    PersonMapper mapper = sqlSession.getMapper(PersonMapper.class);
    Person person1 = new Person("James");
    assertEquals(1, mapper.insert(Collections.singletonList(person1)));
    assertNotNull(person1.getId());
  }
}
