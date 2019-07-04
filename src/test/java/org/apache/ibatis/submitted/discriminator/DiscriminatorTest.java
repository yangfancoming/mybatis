
package org.apache.ibatis.submitted.discriminator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DiscriminatorTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/discriminator/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
      "org/apache/ibatis/submitted/discriminator/CreateDB.sql");
  }

  @Test
  void shouldSwitchResultType() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Vehicle> vehicles = mapper.selectVehicles();
      assertEquals(Car.class, vehicles.get(0).getClass());
      assertEquals(Integer.valueOf(5), ((Car)vehicles.get(0)).getDoorCount());
      assertEquals(Truck.class, vehicles.get(1).getClass());
      assertEquals(Float.valueOf(1.5f), ((Truck)vehicles.get(1)).getCarryingCapacity());
    }
  }

  @Test
  void shouldInheritResultType() {
    // #486
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Owner> owners = mapper.selectOwnersWithAVehicle();
      assertEquals(Truck.class, owners.get(0).getVehicle().getClass());
      assertEquals(Car.class, owners.get(1).getVehicle().getClass());
    }
  }

}
