
package org.apache.ibatis.submitted.dynsql;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynSqlTest {

  protected static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static DynSqlMapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader configReader = Resources.getResourceAsReader("org/apache/ibatis/submitted/dynsql/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(DynSqlMapper.class);

    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/dynsql/CreateDB.sql");
  }

  @Test
  void testSelect() {
    List<Integer> ids = Arrays.asList(1,3,5);
    Parameter parameter = new Parameter();
    parameter.setEnabled(true);
    parameter.setSchema("ibtest");
    parameter.setIds(ids);
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.select", parameter);
    assertEquals(3, answer.size());
  }

  @Test
  void testSelectSimple() {
    List<Integer> ids = Arrays.asList(1,3,5);
    Parameter parameter = new Parameter();
    parameter.setEnabled(true);
    parameter.setSchema("ibtest");
    parameter.setIds(ids);
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.select_simple", parameter);
    assertEquals(3, answer.size());
  }

  @Test  // 测试 bind标签
  void testSelectLike() {
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.selectLike", "Ba");
    assertEquals(2, answer.size());
    assertEquals(4, answer.get(0).get("id"));
    assertEquals(6, answer.get(1).get("id"));
  }

  @Test
  void testNumerics() {
    List<NumericRow> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.selectNumerics");
    assertEquals(1, answer.size());
    NumericRow row = answer.get(0);
    assertEquals(1, (int) row.getId());
    assertEquals(2, (int) row.getTinynumber());
    assertEquals(3, (int) row.getSmallnumber());
    assertEquals(4L, (long) row.getLonginteger());
    assertEquals(new BigInteger("5"), row.getBiginteger());
    assertEquals(new BigDecimal("6.00"), row.getNumericnumber());
    assertEquals(new BigDecimal("7.00"), row.getDecimalnumber());
    assertEquals((Float) 8.0f, row.getRealnumber());
    assertEquals((Float) 9.0f, row.getFloatnumber());
    assertEquals((Double) 10.0, row.getDoublenumber());
  }

  @Test
  void testOgnlStaticMethodCall() {
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.ognlStaticMethodCall", "Rock 'n Roll");
    assertEquals(1, answer.size());
    assertEquals(7, answer.get(0).get("ID"));
  }



  @Test // 测试 if标签
  void testBindNull() {
    List<Map> map1 = mapper.selectDescription(null);// SELECT description FROM ibtest.names WHERE id = 3
    List<Map> map2 = mapper.selectDescription("goat"); // SELECT * FROM ibtest.names
    System.out.println(map1);
    System.out.println(map2);
    assertEquals(1, map1.size());
    assertEquals(7, map2.size());
  }

  /**
   * Verify that can specify any variable name for parameter object when parameter is value object that a type handler exists.
   * https://github.com/mybatis/mybatis-3/issues/1486
   */
  @Test // 测试 if标签
  void testValueObjectWithoutParamAnnotation() {
    List<String> descriptions = mapper.selectDescriptionById(3);
    assertEquals(1, descriptions.size());
    assertEquals("Pebbles", descriptions.get(0));

    List<String> stringList = mapper.selectDescriptionById(null);
    assertEquals(7, stringList.size());
  }



//  @Test
//  public void testWhere() {
//    List<Map> map1 = mapper.selectWhere(3,"Pebbles");//
//    System.out.println(map1);
//  }

  /**
   * Variations for with https://github.com/mybatis/mybatis-3/issues/1486
   */
  @Test
  void testNonValueObjectWithoutParamAnnotation() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      DynSqlMapper mapper = sqlSession.getMapper(DynSqlMapper.class);
      DynSqlMapper.Conditions conditions = new DynSqlMapper.Conditions();
      conditions.setId(3);
      List<String> descriptions = mapper.selectDescriptionByConditions(conditions);
      assertEquals(1, descriptions.size());
      assertEquals("Pebbles", descriptions.get(0));

      assertEquals(7, mapper.selectDescriptionByConditions(null).size());
      assertEquals(7, mapper.selectDescriptionByConditions(new DynSqlMapper.Conditions()).size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      DynSqlMapper mapper = sqlSession.getMapper(DynSqlMapper.class);
      DynSqlMapper.Conditions conditions = new DynSqlMapper.Conditions();
      conditions.setId(3);
      try {
        mapper.selectDescriptionByConditions2(conditions);
      } catch (PersistenceException e) {
        assertEquals("There is no getter for property named 'conditions' in 'class org.apache.ibatis.submitted.dynsql.DynSqlMapper$Conditions'", e.getCause().getMessage());
      }
      assertEquals(7, mapper.selectDescriptionByConditions2(null).size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      DynSqlMapper mapper = sqlSession.getMapper(DynSqlMapper.class);
      DynSqlMapper.Conditions conditions = new DynSqlMapper.Conditions();
      conditions.setId(3);
      try {
        mapper.selectDescriptionByConditions3(conditions);
      } catch (PersistenceException e) {
        assertEquals("There is no getter for property named 'conditions' in 'class org.apache.ibatis.submitted.dynsql.DynSqlMapper$Conditions'", e.getCause().getMessage());
      }
      assertEquals(7, mapper.selectDescriptionByConditions3(null).size());
    }

  }

  /**
   * Variations for with https://github.com/mybatis/mybatis-3/issues/1486
   */
  @Test
  void testCustomValueObjectWithoutParamAnnotation() throws IOException {
    SqlSessionFactory sqlSessionFactory;
    try (Reader configReader = Resources.getResourceAsReader("org/apache/ibatis/submitted/dynsql/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);
      // register type handler for the user defined class (= value object)
      sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(DynSqlMapper.Conditions.class, new TypeHandler<DynSqlMapper.Conditions>() {
        @Override
        public void setParameter(PreparedStatement ps, int i, DynSqlMapper.Conditions parameter, JdbcType jdbcType) throws SQLException {
          if (parameter.getId() != null) {
            ps.setInt(i, parameter.getId());
          } else {
            ps.setNull(i, JdbcType.INTEGER.TYPE_CODE);
          }
        }
        @Override
        public DynSqlMapper.Conditions getResult(ResultSet rs, String columnName) throws SQLException {
          return null;
        }
        @Override
        public DynSqlMapper.Conditions getResult(ResultSet rs, int columnIndex) throws SQLException {
          return null;
        }
        @Override
        public DynSqlMapper.Conditions getResult(CallableStatement cs, int columnIndex) throws SQLException {
          return null;
        }
      });
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      DynSqlMapper mapper = sqlSession.getMapper(DynSqlMapper.class);
      DynSqlMapper.Conditions conditions = new DynSqlMapper.Conditions();
      conditions.setId(3);
      List<String> descriptions = mapper.selectDescriptionByConditions(conditions);
      assertEquals(1, descriptions.size());
      assertEquals("Pebbles", descriptions.get(0));

      assertEquals(7, mapper.selectDescriptionByConditions(null).size());
      assertEquals(7, mapper.selectDescriptionByConditions(new DynSqlMapper.Conditions()).size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      DynSqlMapper mapper = sqlSession.getMapper(DynSqlMapper.class);
      DynSqlMapper.Conditions conditions = new DynSqlMapper.Conditions();
      conditions.setId(3);
      List<String> descriptions = mapper.selectDescriptionByConditions2(conditions);
      assertEquals(1, descriptions.size());
      assertEquals("Pebbles", descriptions.get(0));

      assertEquals(7, mapper.selectDescriptionByConditions2(null).size());
      assertEquals(0, mapper.selectDescriptionByConditions2(new DynSqlMapper.Conditions()).size());
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      DynSqlMapper mapper = sqlSession.getMapper(DynSqlMapper.class);
      DynSqlMapper.Conditions conditions = new DynSqlMapper.Conditions();
      conditions.setId(3);
      List<String> descriptions = mapper.selectDescriptionByConditions3(conditions);
      assertEquals(1, descriptions.size());
      assertEquals("Pebbles", descriptions.get(0));

      assertEquals(7, mapper.selectDescriptionByConditions3(null).size());
      assertEquals(7, mapper.selectDescriptionByConditions3(new DynSqlMapper.Conditions()).size());
    }
  }




}
