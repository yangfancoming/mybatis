
package org.apache.ibatis.submitted.discriminator;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscriminatorTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/discriminator/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/discriminator/CreateDB.sql");
  }

  @Test
  void shouldSwitchResultType() {
    List<Vehicle> vehicles = mapper.selectVehicles();
    assertEquals(Car.class, vehicles.get(0).getClass());
    assertEquals(Integer.valueOf(5), ((Car)vehicles.get(0)).getDoorCount());
    assertEquals(Truck.class, vehicles.get(1).getClass());
    assertEquals(Float.valueOf(1.5f), ((Truck)vehicles.get(1)).getCarryingCapacity());
  }

  @Test
  void shouldInheritResultType() {
    // #486
    List<Owner> owners = mapper.selectOwnersWithAVehicle();
    assertEquals(Truck.class, owners.get(0).getVehicle().getClass());
    assertEquals(Car.class, owners.get(1).getVehicle().getClass());
  }
}
