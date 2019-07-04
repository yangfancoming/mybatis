
package org.apache.ibatis.submitted.ancestor_ref;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AncestorRefTest {
  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    // create an SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/ancestor_ref/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/ancestor_ref/CreateDB.sql");
  }

  @Test
  void testCircularAssociation() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserAssociation(1);
      assertEquals("User2", user.getFriend().getName());
    }
  }

  @Test
  void testCircularCollection() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      User user = mapper.getUserCollection(2);
      assertEquals("User2", user.getFriends().get(0).getName());
      assertEquals("User3", user.getFriends().get(1).getName());
    }
  }

  @Test
  void testAncestorRef() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      Blog blog = mapper.selectBlog(1);
      assertEquals("Author1", blog.getAuthor().getName());
      assertEquals("Author2", blog.getCoAuthor().getName());
      // author and coauthor should have a ref to blog
      assertEquals(blog, blog.getAuthor().getBlog());
      assertEquals(blog, blog.getCoAuthor().getBlog());
      // reputation should point to it author? or fail but do not point to a random one
      assertEquals(blog.getAuthor(), blog.getAuthor().getReputation().getAuthor());
      assertEquals(blog.getCoAuthor(), blog.getCoAuthor().getReputation().getAuthor());
    }
  }
}
