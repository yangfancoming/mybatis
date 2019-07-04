
package org.apache.ibatis.submitted.multidb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;

class ProviderTest {

  @Test
  void shouldUseDefaultId() throws Exception {
    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multidb/MultiDbConfig.xml");
    DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) new SqlSessionFactoryBuilder().build(reader);
    Configuration c = sqlSessionFactory.getConfiguration();
    assertEquals("hsql", c.getDatabaseId());
  }

  @Test
  void shouldUseProvider() throws Exception {
    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multidb/ProviderConfig.xml");
    DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) new SqlSessionFactoryBuilder().build(reader);
    Configuration c = sqlSessionFactory.getConfiguration();
    assertEquals("translated", c.getDatabaseId());
  }
}
