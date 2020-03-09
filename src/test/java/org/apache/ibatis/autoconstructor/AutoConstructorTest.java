
package org.apache.ibatis.autoconstructor;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutoConstructorTest extends MyBaseDataTest {

  public static  AutoConstructorMapper mapper ;

  public static final String XMLPATH = "org/apache/ibatis/autoconstructor/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/autoconstructor/CreateDB.sql";

  public AutoConstructorTest() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    mapper = sqlSession.getMapper(AutoConstructorMapper.class);
  }

  @Test
  void fullyPopulatedSubject() {
      final Object subject = mapper.getSubject(1);
      assertNotNull(subject);
  }

  @Test
  void primitiveSubjects() {
    List<PrimitiveSubject> subjects = mapper.getSubjects();
    System.out.println(subjects);
    assertThrows(PersistenceException.class, mapper::getSubjects);
  }

  @Test
  void annotatedSubject() {
      verifySubjects(mapper.getAnnotatedSubjects());
  }

  @Test
  void badSubject() {
    assertThrows(PersistenceException.class, mapper::getBadSubjects);
  }

  @Test
  void extensiveSubject() {
    verifySubjects(mapper.getExtensiveSubject());
  }

  private void verifySubjects(final List<?> subjects) {
    assertNotNull(subjects);
    Assertions.assertThat(subjects.size()).isEqualTo(3);
  }
}
