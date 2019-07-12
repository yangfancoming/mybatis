
package org.apache.ibatis.submitted.keygen;

import static com.googlecode.catchexception.apis.BDDCatchException.*;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class Jdbc3KeyGeneratorTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static CountryMapper mapper;


  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/keygen/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession(true);// 这里如果不添加 true 参数  shouldAssignKeyToBean 方法最终不会插入库
      mapper = sqlSession.getMapper(CountryMapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/keygen/CreateDB.sql");
  }
  @AfterEach
  public void after(){
    sqlSession.rollback();
  }

  @Test
  void shouldAssignKeyToBean() {
    Country country = new Country("China", "CN");
    System.out.println(mapper.insertBean(country));
    // 返回自定生成的主键id
    assertNotNull(country.getId());
  }

  /**
   * 测试 在 penSession(true) 自动提交模式中，执行完sql语句之后就可以拿到 id
   */
  @Test
  void shouldAssignKeyToBean_batch1() {
      Country country1 = new Country("China", "CN");
      mapper.insertBean(country1);
      Country country2 = new Country("Canada", "CA");
      mapper.insertBean(country2);
      sqlSession.flushStatements();
      sqlSession.clearCache();
      assertNotNull(country1.getId());
      assertNotNull(country2.getId());
  }

  /**
   * 测试 在 ExecutorType.BATCH 模式中，只有在执行了 flushStatements 之后 才能返回主键id
  */
  @Test
  void shouldAssignKeyToBean_batch2() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      try {
        CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
        Country country1 = new Country("China", "CN");
        mapper.insertBean(country1);
        System.out.println(country1.getId()); // null
        Country country2 = new Country("Canada", "CA");
        mapper.insertBean(country2);
        System.out.println(country1.getId()); // null
        sqlSession.flushStatements(); // 执行这句代码后  才能返回主键id
        sqlSession.clearCache();
        assertEquals(1,country1.getId());
        assertEquals(2,country2.getId());
        assertNotNull(country2.getId());
      } finally {
        sqlSession.rollback(); // 数据将不会插入
      }
    }
  }


  @Test
  void shouldAssignKeyToNamedBean() {
    Country country = new Country("China", "CN");
    mapper.insertNamedBean(country);
    assertNotNull(country.getId());
  }

  @Test
  void shouldAssignKeyToNamedBean_batch() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      try {
        CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
        Country country1 = new Country("China", "CN");
        mapper.insertNamedBean(country1);
        Country country2 = new Country("Canada", "CA");
        mapper.insertNamedBean(country2);
        sqlSession.flushStatements();
        sqlSession.clearCache();
        assertNotNull(country1.getId());
        assertNotNull(country2.getId());
      } finally {
        sqlSession.rollback();
      }
    }
  }

  @Test
  void shouldAssignKeyToNamedBean_keyPropertyWithParamName() {
    Country country = new Country("China", "CN");
    mapper.insertNamedBean_keyPropertyWithParamName(country);
    assertNotNull(country.getId());
  }

  @Test
  void shouldAssignKeyToNamedBean_keyPropertyWithParamName_batch() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
        CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
        Country country1 = new Country("China", "CN");
        mapper.insertNamedBean_keyPropertyWithParamName(country1);
        Country country2 = new Country("Canada", "CA");
        mapper.insertNamedBean_keyPropertyWithParamName(country2);
        sqlSession.flushStatements();
        sqlSession.clearCache();
        assertNotNull(country1.getId());
        assertNotNull(country2.getId());
    }
  }

  /**  */
  @Test
  void shouldAssignKeysToList() {
    List<Country> countries = new ArrayList<>();
    countries.add(new Country("China", "CN"));
    countries.add(new Country("United Kiongdom", "GB"));
    countries.add(new Country("United States of America", "US"));
    mapper.insertList(countries);
    countries.forEach(x->  assertNotNull(x.getId()) );
  }

  @Test
  void shouldAssignKeysToNamedList() {
    List<Country> countries = new ArrayList<>();
    countries.add(new Country("China", "CN"));
    countries.add(new Country("United Kiongdom", "GB"));
    countries.add(new Country("United States of America", "US"));
//    collection="countries"
    mapper.insertNamedList(countries);
    countries.forEach(x->  assertNotNull(x.getId()) );
  }

  @Test
  void shouldAssingKeysToCollection() {
    Set<Country> countries = new HashSet<>();
    countries.add(new Country("China", "CN"));
    countries.add(new Country("United Kiongdom", "GB"));
    mapper.insertSet(countries);
    countries.forEach(x->  assertNotNull(x.getId()) );
  }

  @Test
  void shouldAssingKeysToNamedCollection() {
    Set<Country> countries = new HashSet<>();
    countries.add(new Country("China", "CN"));
    countries.add(new Country("United Kiongdom", "GB"));
    mapper.insertNamedSet(countries);
    countries.forEach(x->  assertNotNull(x.getId()) );
  }

  @Test
  void shouldAssingKeysToArray() {
    Country[] countries = new Country[2];
    countries[0] = new Country("China", "CN");
    countries[1] = new Country("United Kiongdom", "GB");
    // collection="array"
    mapper.insertArray(countries);
    Arrays.stream(countries).forEach(x-> assertNotNull(x.getId()));

  }

  @Test
  void shouldAssingKeysToNamedArray() {
    Country[] countries = new Country[2];
    countries[0] = new Country("China", "CN");
    countries[1] = new Country("United Kiongdom", "GB");
    mapper.insertNamedArray(countries);
    Arrays.stream(countries).forEach(x-> assertNotNull(x.getId()));
  }

  @Test
  void shouldAssignKeyToBean_MultiParams() {
    Country country = new Country("China", "CN");
    mapper.insertMultiParams(country, 1);
    assertNotNull(country.getId());
  }

  @Test
  void shouldFailIfKeyPropertyIsInvalid_NoParamName() {
    Country country = new Country("China", "CN");
    when(mapper).insertMultiParams_keyPropertyWithoutParamName(country, 1);
    then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
      "Could not determine which parameter to assign generated keys to. "
        + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
        + "Specified key properties are [id] and available parameters are [");
  }

  @Test
  void shouldFailIfKeyPropertyIsInvalid_WrongParamName() {
    Country country = new Country("China", "CN");
    when(mapper).insertMultiParams_keyPropertyWithWrongParamName(country, 1);
    then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
      "Could not find parameter 'bogus'. "
        + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
        + "Specified key properties are [bogus.id] and available parameters are [");
  }

  @Test
  void shouldAssignKeysToNamedList_MultiParams() {
    List<Country> countries = new ArrayList<>();
    countries.add(new Country("China", "CN"));
    countries.add(new Country("United Kiongdom", "GB"));
    mapper.insertList_MultiParams(countries, 1);
    countries.forEach(x->  assertNotNull(x.getId()) );
  }

  @Test
  void shouldAssignKeysToNamedCollection_MultiParams() {
    Set<Country> countries = new HashSet<>();
    countries.add(new Country("China", "CN"));
    countries.add(new Country("United Kiongdom", "GB"));
    mapper.insertSet_MultiParams(countries, 1);
    for (Country country : countries) {
      assertNotNull(country.getId());
    }
  }

  @Test
  void shouldAssignKeysToNamedArray_MultiParams() {
    Country[] countries = new Country[2];
    countries[0] = new Country("China", "CN");
    countries[1] = new Country("United Kiongdom", "GB");
    mapper.insertArray_MultiParams(countries, 1);
    Arrays.stream(countries).forEach(x-> assertNotNull(x.getId()));
  }

  @Test
  void shouldAssignMultipleGeneratedKeysToABean() {
    Planet planet = new Planet();
    planet.setName("pluto");
    mapper.insertPlanet(planet);
    assertEquals("pluto-" + planet.getId(), planet.getCode());
  }

  @Test
  void shouldAssignMultipleGeneratedKeysToBeans() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      try {

        Planet planet1 = new Planet();
        planet1.setName("pluto");
        Planet planet2 = new Planet();
        planet2.setName("neptune");
        List<Planet> planets = Arrays.asList(planet1, planet2);
        mapper.insertPlanets(planets);
        assertEquals("pluto-" + planet1.getId(), planet1.getCode());
        assertEquals("neptune-" + planet2.getId(), planet2.getCode());
      } finally {
        sqlSession.rollback();
      }
    }
  }

  @Test
  void shouldAssignMultipleGeneratedKeysToABean_MultiParams() {
    Planet planet = new Planet();
    planet.setName("pluto");
    mapper.insertPlanet_MultiParams(planet, 1);
    assertEquals("pluto-" + planet.getId(), planet.getCode());
  }
  @Test
  void shouldAssignMultipleGeneratedKeysToABean_MultiParams_batch() {
    Planet planet1 = new Planet();
    planet1.setName("pluto");
    mapper.insertPlanet_MultiParams(planet1, 1);
    Planet planet2 = new Planet();
    planet2.setName("neptune");
    mapper.insertPlanet_MultiParams(planet2, 1);
    sqlSession.flushStatements();
    sqlSession.clearCache();
    assertEquals("pluto-" + planet1.getId(), planet1.getCode());
    assertEquals("neptune-" + planet2.getId(), planet2.getCode());
  }

  @Test
  void shouldAssignMultipleGeneratedKeysToBeans_MultiParams() {
    Planet planet1 = new Planet();
    planet1.setName("pluto");
    Planet planet2 = new Planet();
    planet2.setName("neptune");
    List<Planet> planets = Arrays.asList(planet1, planet2);
    mapper.insertPlanets_MultiParams(planets, 1);
    assertEquals("pluto-" + planet1.getId(), planet1.getCode());
    assertEquals("neptune-" + planet2.getId(), planet2.getCode());
  }

  @Test
  void assigningMultipleKeysToDifferentParams() {
    Planet planet = new Planet();
    planet.setName("pluto");
    Map<String, Object> map = new HashMap<>();
    mapper.insertAssignKeysToTwoParams(planet, map);
    assertNotNull(planet.getId());
    assertNotNull(map.get("code"));
  }

  @Test
  void assigningMultipleKeysToDifferentParams_batch() {
    Planet planet1 = new Planet();
    planet1.setName("pluto");
    Map<String, Object> map1 = new HashMap<>();
    mapper.insertAssignKeysToTwoParams(planet1, map1);
    Planet planet2 = new Planet();
    planet2.setName("pluto");
    Map<String, Object> map2 = new HashMap<>();
    mapper.insertAssignKeysToTwoParams(planet2, map2);
    sqlSession.flushStatements();
    sqlSession.clearCache();
    assertNotNull(planet1.getId());
    assertNotNull(map1.get("code"));
    assertNotNull(planet2.getId());
    assertNotNull(map2.get("code"));
  }

  @Test
  void shouldErrorUndefineProperty() {
    int i = mapper.insertUndefineKeyProperty(new Country("China", "CN"));
    System.out.println(i);
    //    when(mapper).insertUndefineKeyProperty(new Country("China", "CN"));
    //    then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
    //      "### Error updating database.  Cause: org.apache.ibatis.executor.ExecutorException: Error getting generated key or setting result to parameter object. Cause: org.apache.ibatis.executor.ExecutorException: No setter found for the keyProperty 'country_id' in 'org.apache.ibatis.submitted.keygen.Country'.");
  }

  @Test
  void shouldFailIfTooManyGeneratedKeys() {
    when(mapper).tooManyGeneratedKeys(new Country());
    then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
      "Too many keys are generated. There are only 1 target objects.");
  }

  @Test
  void shouldFailIfTooManyGeneratedKeys_ParamMap() {
    when(mapper).tooManyGeneratedKeysParamMap(new Country(), 1);
    then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
      "Too many keys are generated. There are only 1 target objects.");
  }

  @Test
  void shouldFailIfTooManyGeneratedKeys_Batch() {
    mapper.tooManyGeneratedKeysParamMap(new Country(), 1);
    mapper.tooManyGeneratedKeysParamMap(new Country(), 1);
    when(sqlSession).flushStatements();
    then(caughtException()).isInstanceOf(PersistenceException.class).hasMessageContaining(
      "Too many keys are generated. There are only 2 target objects.");
  }
}
