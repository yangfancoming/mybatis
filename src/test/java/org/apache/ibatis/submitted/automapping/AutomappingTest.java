
package org.apache.ibatis.submitted.automapping;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class AutomappingTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/automapping/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/automapping/CreateDB.sql";
  Mapper mapper;

  @BeforeEach
  public void beforeAll() throws Exception {
    setUpByInputStream(XMLPATH,DBSQL);
    mapper = sqlSession.getMapper(Mapper.class);
  }

  @Test
  void shouldGetAUser() {
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.NONE);
    User user = mapper.getUser(1);
    Assertions.assertEquals("User1", user.getName());
  }

  @Test
  void shouldGetAUserWhithPhoneNumber() {
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.NONE);
    User user = mapper.getUserWithPhoneNumber(1);
    Assertions.assertEquals("User1", user.getName());
    Assertions.assertEquals(Long.valueOf(12345678901L), user.getPhone());
  }

  @Test
  void shouldNotInheritAutoMappingInherited_InlineNestedResultMap() {
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.NONE);
    User user = mapper.getUserWithPets_Inline(2);
    Assertions.assertEquals(Integer.valueOf(2), user.getId());
    Assertions.assertEquals("User2", user.getName());
    Assertions.assertNull(user.getPets().get(0).getPetName(), "should not inherit auto-mapping");
    Assertions.assertEquals("John", user.getPets().get(0).getBreeder().getBreederName());
  }

  @Test
  void shouldNotInheritAutoMappingInherited_ExternalNestedResultMap() {
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.NONE);
    User user = mapper.getUserWithPets_External(2);
    Assertions.assertEquals(Integer.valueOf(2), user.getId());
    Assertions.assertEquals("User2", user.getName());
    Assertions.assertNull(user.getPets().get(0).getPetName(), "should not inherit auto-mapping");
    Assertions.assertEquals("John", user.getPets().get(0).getBreeder().getBreederName());
  }

  @Test
  void shouldIgnorePartialAutoMappingBehavior_InlineNestedResultMap() {
    // For nested resultMaps, PARTIAL works the same as NONE
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
    User user = mapper.getUserWithPets_Inline(2);
    Assertions.assertEquals(Integer.valueOf(2), user.getId());
    Assertions.assertEquals("User2", user.getName());
    Assertions.assertNull(user.getPets().get(0).getPetName(), "should not inherit auto-mapping");
    Assertions.assertEquals("John", user.getPets().get(0).getBreeder().getBreederName());
  }

  @Test
  void shouldRespectFullAutoMappingBehavior_InlineNestedResultMap() {
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.FULL);
    User user = mapper.getUserWithPets_Inline(2);
    Assertions.assertEquals(Integer.valueOf(2), user.getId());
    Assertions.assertEquals("User2", user.getName());
    Assertions.assertEquals("Chien", user.getPets().get(0).getPetName());
    Assertions.assertEquals("John", user.getPets().get(0).getBreeder().getBreederName());
  }

  @Test
  void shouldIgnorePartialAutoMappingBehavior_ExternalNestedResultMap() {
    // For nested resultMaps, PARTIAL works the same as NONE
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
    User user = mapper.getUserWithPets_External(2);
    Assertions.assertEquals(Integer.valueOf(2), user.getId());
    Assertions.assertEquals("User2", user.getName());
    Assertions.assertNull(user.getPets().get(0).getPetName(), "should not inherit auto-mapping");
    Assertions.assertEquals("John", user.getPets().get(0).getBreeder().getBreederName());
  }

  @Test
  void shouldRespectFullAutoMappingBehavior_ExternalNestedResultMap() {
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.FULL);
    User user = mapper.getUserWithPets_External(2);
    Assertions.assertEquals(Integer.valueOf(2), user.getId());
    Assertions.assertEquals("User2", user.getName());
    Assertions.assertEquals("Chien", user.getPets().get(0).getPetName());
    Assertions.assertEquals("John", user.getPets().get(0).getBreeder().getBreederName());
  }

  @Test
  void shouldGetBooks() {
    // set automapping to default partial
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
    // no errors throw
    List<Book> books = mapper.getBooks();
    Assertions.assertTrue(!books.isEmpty(), "should return results,no errors throw");
  }

  @Test
  void shouldUpdateFinalField() {
    // set automapping to default partial
    sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
    Article article = mapper.getArticle();
    // Java Language Specification 17.5.3 Subsequent Modification of Final Fields
    // http://docs.oracle.com/javase/specs/jls/se5.0/html/memory.html#17.5.3
    // The final field should be updated in mapping
    Assertions.assertTrue(article.version > 0, "should update version in mapping");
  }
}
