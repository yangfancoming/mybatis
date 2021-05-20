
package org.apache.ibatis.submitted.refid_resolution;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Reader;

public class RefidResolutionTest {
  @Test
  public void testIncludes() throws Exception {
    String resource = "org/apache/ibatis/submitted/refid_resolution/MapperConfig.xml";
    Reader reader = Resources.getResourceAsReader(resource);
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    SqlSessionFactory sqlSessionFactory = builder.build(reader);
    Assertions.assertThrows(PersistenceException.class, () -> {
      sqlSessionFactory.getConfiguration().getMappedStatementNames();
    });
  }
}
