
package org.apache.ibatis.submitted.inline_association_with_dot;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InlineCollectionWithDotTest {

  private SqlSession sqlSession;

  public void openSession(String aConfig) throws Exception {

    final String resource = "org/apache/ibatis/submitted/inline_association_with_dot/ibatis-" + aConfig + ".xml";
    try (Reader batisConfigReader = Resources.getResourceAsReader(resource)) {

      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(batisConfigReader);

      BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
              "org/apache/ibatis/submitted/inline_association_with_dot/create.sql");

      sqlSession = sqlSessionFactory.openSession();
    }
  }

  @AfterEach
  void closeSession() {
    if (sqlSession != null) {
      sqlSession.close();
    }
  }

  /*
   * Load an element with an element with and element with a value. Expect that this is
   * possible bij using an inline 'association' map.
   */
  @Test
  void selectElementValueInContainerUsingInline()
  throws Exception {
    openSession("inline");

    Element myElement = sqlSession.getMapper(ElementMapperUsingInline.class).selectElement();

    assertEquals("value", myElement.getElement().getElement().getValue());
  }

  /*
   * Load an element with an element with and element with a value. Expect that this is
   * possible bij using an sub-'association' map.
   */
  @Test
  void selectElementValueInContainerUsingSubMap() throws Exception {
   openSession("submap");

   Element myElement = sqlSession.getMapper(ElementMapperUsingSubMap.class).selectElement();

   assertEquals("value", myElement.getElement().getElement().getValue());
  }
}
