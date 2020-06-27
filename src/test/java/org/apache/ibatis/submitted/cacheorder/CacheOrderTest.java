
package org.apache.ibatis.submitted.cacheorder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CacheOrderTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/cacheorder/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/cacheorder/CreateDB.sql");
  }

  @Test
  void shouldResolveACacheRefNotYetRead() {
    MappedStatement ms = sqlSessionFactory.getConfiguration().getMappedStatement("getUser");
    Cache cache = ms.getCache();
    assertEquals("org.apache.ibatis.submitted.cacheorder.Mapper2", cache.getId());
  }
}
