
package org.apache.ibatis.submitted.collectionparameters;

import java.io.Reader;
import java.util.*;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CollectionParametersTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession sqlSession;
  private static Mapper mapper;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/collectionparameters/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession();
      mapper = sqlSession.getMapper(Mapper.class);

    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/collectionparameters/CreateDB.sql");
  }

  /**
   * 集合参数 之
   * list参数   使用list作为参数
  */
  @Test
  void shouldGetTwoUsersPassingAList() {
    List<Integer> list = Arrays.asList(1,2);
    List<User> users = mapper.getUsersFromList(list);
    Assertions.assertEquals(2, users.size());
  }

  /**
   * 集合参数 之
   * array参数   使用数组作为参数
   */
  @Test
  void shouldGetTwoUsersPassingAnArray() {
    Integer[] list = new Integer[2];
    list[0] = 1;
    list[1] = 2;
    List<User> users = mapper.getUsersFromArray(list);
    Assertions.assertEquals(2, users.size());
  }

  /**
   * 集合参数 之
   * set参数   使用set作为参数
   */
  @Test
  void shouldGetTwoUsersPassingACollection() {
    Set<Integer> list = new HashSet<>();
    list.add(1);
    list.add(2);
    List<User> users = mapper.getUsersFromCollection(list);
    Assertions.assertEquals(2, users.size());
  }

}
