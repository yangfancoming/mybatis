
package org.apache.ibatis.submitted.parent_childs;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

class ParentChildTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/parent_childs/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/parent_childs/CreateDB.sql");
  }

  @Test
  void shouldGetAUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Parent> parents = mapper.getParents();
      Assertions.assertEquals(2, parents.size());
      Parent firstParent = parents.get(0);
      Assertions.assertEquals("Jose", firstParent.getName());
      Assertions.assertEquals(2, firstParent.getChilds().size());
      Parent secondParent = parents.get(1);
      Assertions.assertEquals("Juan", secondParent.getName());
      Assertions.assertEquals(0, secondParent.getChilds().size()); // note an empty list is inyected
    }
  }

}
