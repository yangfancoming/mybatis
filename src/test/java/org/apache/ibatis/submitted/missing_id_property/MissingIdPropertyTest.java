
package org.apache.ibatis.submitted.missing_id_property;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MissingIdPropertyTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/missing_id_property/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/missing_id_property/CreateDB.sql");
  }

  @Test
  void shouldMapResultsWithoutActuallyWritingIdProperties() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
      Car car = carMapper.getCarsInfo(1L);
      Assertions.assertNotNull(car.getName());
      Assertions.assertNotNull(car.getCarParts());
      Assertions.assertEquals(3, car.getCarParts().size());
    }
  }

}
