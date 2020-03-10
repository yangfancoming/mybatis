
package org.apache.ibatis.submitted.ancestor_ref;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AncestorRefTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/ancestor_ref/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/ancestor_ref/CreateDB.sql";

  public static  Mapper mapper ;

  public AncestorRefTest() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    mapper = sqlSession.getMapper(Mapper.class);
  }

  @Test
  void testCircularAssociation() {
    User user = mapper.getUserAssociation(1);
    assertEquals("User2", user.getFriend().getName());
  }

  @Test
  void testCircularCollection() {
    User user = mapper.getUserCollection(2);
    assertEquals("User2", user.getFriends().get(0).getName());
    assertEquals("User3", user.getFriends().get(1).getName());
  }

  @Test
  void testAncestorRef() {
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
