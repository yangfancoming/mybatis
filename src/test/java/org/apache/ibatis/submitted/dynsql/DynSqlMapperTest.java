
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynSqlMapperTest {

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
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), "org/apache/ibatis/submitted/dynsql/CreateDB.sql");
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

  /**   测试 if标签
   * 不使用 @param 注解的情况 传入参数
   * SELECT description FROM ibtest.names WHERE id = ?
   * Verify that can specify any variable name for parameter object when parameter is value object that a type handler exists.
   * https://github.com/mybatis/mybatis-3/issues/1486
   */
  @Test
  void testValueObjectWithoutParamAnnotationWithCondition() {
    List<String> descriptions = mapper.selectDescriptionById(3);
    assertEquals(1, descriptions.size());
    assertEquals("Pebbles", descriptions.get(0));
  }

  /**  测试 if标签
   * 不使用 @param 注解的情况 传入参数 为null
   * SELECT description FROM ibtest.names
  */
  @Test
  void testValueObjectWithoutParamAnnotationWithoutCondition() {
    List<String> stringList = mapper.selectDescriptionById(null);
    assertEquals(7, stringList.size());
  }

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
