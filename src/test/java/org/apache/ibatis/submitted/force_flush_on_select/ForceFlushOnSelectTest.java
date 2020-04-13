
package org.apache.ibatis.submitted.force_flush_on_select;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.session.LocalCacheScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ForceFlushOnSelectTest extends MyBaseDataTest {


  public static final String XMLPATH = "org/apache/ibatis/submitted/force_flush_on_select/ibatisConfig.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/force_flush_on_select/CreateDB.sql";

  PersonMapper personMapper;

  @BeforeEach
  public void beforeAll() throws Exception {
    setUpByInputStream(XMLPATH,DBSQL);
    personMapper = sqlSession.getMapper(PersonMapper.class);
  }

  /**
   *     <select id="selectByIdFlush" resultMap="personMap" parameterType="int" flushCache="true">
   *         SELECT id, firstName, lastName FROM person WHERE id = #{id}
   *     </select>
  */
  @Test
  void testShouldFlushLocalSessionCacheOnQuery() throws SQLException {
    personMapper.selectByIdFlush(1);
    updateDatabase(sqlSession.getConnection());
    Person updatedPerson = personMapper.selectByIdFlush(1);
    assertEquals("Simone", updatedPerson.getFirstName());
  }

  /**
   *     <select id="selectByIdNoFlush" resultMap="personMap" parameterType="int">
   *       SELECT id, firstName, lastName FROM person WHERE id = #{id}
   *     </select>
  */
  @Test
  void testShouldNotFlushLocalSessionCacheOnQuery() throws SQLException {
    personMapper.selectByIdNoFlush(1);
    updateDatabase(sqlSession.getConnection());
    Person updatedPerson = personMapper.selectByIdNoFlush(1);
    assertEquals("John", updatedPerson.getFirstName());
  }

  @Test
  void testShouldFlushLocalSessionCacheOnQueryForList() throws SQLException {
    List<Person> people = personMapper.selectAllFlush();
    updateDatabase(sqlSession.getConnection());
    people = personMapper.selectAllFlush();
    assertEquals("Simone", people.get(0).getFirstName());
  }

  @Test
  void testShouldNotFlushLocalSessionCacheOnQueryForList() throws SQLException {
    List<Person> people = personMapper.selectAllNoFlush();
    updateDatabase(sqlSession.getConnection());
    people = personMapper.selectAllNoFlush();
    assertEquals("John", people.get(0).getFirstName());
  }

  private void updateDatabase(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.executeUpdate("UPDATE person SET firstName = 'Simone' WHERE id = 1");
    }
  }

  @Test
  void testUpdateShouldFlushLocalCache() {
    Person person = personMapper.selectByIdNoFlush(1);
    person.setLastName("Perez"); // it is ignored in update
    personMapper.update(person);
    Person updatedPerson = personMapper.selectByIdNoFlush(1);
    assertEquals("Smith", updatedPerson.getLastName());
    assertNotSame(person, updatedPerson);
  }

  @Test
  void testSelectShouldFlushLocalCacheIfFlushLocalCacheAtferEachStatementIsTrue() throws SQLException {
    sqlSessionFactory.getConfiguration().setLocalCacheScope(LocalCacheScope.STATEMENT);
    PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
    List<Person> people = personMapper.selectAllNoFlush();
    updateDatabase(sqlSession.getConnection());
    people = personMapper.selectAllFlush();
    assertEquals("Simone", people.get(0).getFirstName());
  }

}
