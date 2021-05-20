
package org.apache.ibatis.submitted.not_null_column;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;

class NotNullColumnTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static FatherMapper fatherMapper;

  @BeforeAll
  static void initDatabase() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/not_null_column/ibatisConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      fatherMapper = sqlSession.getMapper(FatherMapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/not_null_column/CreateDB.sql");
  }

  @Test
  void testNotNullColumnWithChildrenNoFid() {
    Father test = fatherMapper.selectByIdNoFid(1);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertEquals(2, test.getChildren().size());
  }

  @Test
  void testNotNullColumnWithoutChildrenNoFid() {
    Father test = fatherMapper.selectByIdNoFid(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }

  @Test
  void testNotNullColumnWithoutChildrenFid() {
    Father test = fatherMapper.selectByIdFid(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }

  @Test
  void testNotNullColumnWithoutChildrenWithInternalResultMap() {
    Father test = fatherMapper.selectByIdWithInternalResultMap(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }

  @Test
  void testNotNullColumnWithoutChildrenWithRefResultMap() {
    Father test = fatherMapper.selectByIdWithRefResultMap(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }

  @Test
  void testNotNullColumnWithoutChildrenFidMultipleNullColumns() {
    Father test = fatherMapper.selectByIdFidMultipleNullColumns(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }

  @Test
  void testNotNullColumnWithoutChildrenFidMultipleNullColumnsAndBrackets() {
    Father test = fatherMapper.selectByIdFidMultipleNullColumnsAndBrackets(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }

  @Test
  void testNotNullColumnWithoutChildrenFidWorkaround() {
    Father test = fatherMapper.selectByIdFidWorkaround(2);
    assertNotNull(test);
    assertNotNull(test.getChildren());
    assertTrue(test.getChildren().isEmpty());
  }
}
