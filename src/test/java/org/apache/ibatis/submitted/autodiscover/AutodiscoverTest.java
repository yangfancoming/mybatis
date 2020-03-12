
package org.apache.ibatis.submitted.autodiscover;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Reader;
import java.math.BigInteger;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.autodiscover.aliases.DummyTypeAlias;
import org.apache.ibatis.submitted.autodiscover.mappers.DummyMapper;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AutodiscoverTest {

  protected static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setup() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/autodiscover/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
  }

  // 测试注解别名
  @Test
  void testTypeAlias() {
    TypeAliasRegistry typeAliasRegistry = sqlSessionFactory.getConfiguration().getTypeAliasRegistry();
    Class<Object> testAlias = typeAliasRegistry.resolveAlias("testAlias");
    Assert.assertTrue(testAlias.getName().equals(DummyTypeAlias.class.getName()));
  }

  @Test
  void testTypeHandler() {
    TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
    assertTrue(typeHandlerRegistry.hasTypeHandler(BigInteger.class));
  }

  @Test
  void testMapper() {
    assertTrue(sqlSessionFactory.getConfiguration().hasMapper(DummyMapper.class));
  }
}
