
package org.apache.ibatis.submitted.global_variables_defaults;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.JdbcType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

class CustomizationTest {

  @Test
  void applyDefaultValueWhenCustomizeDefaultValueSeparator() throws IOException {
    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty(PropertyParser.KEY_DEFAULT_VALUE_SEPARATOR, "?:");
    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config-custom-separator.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();
    configuration.addMapper(CustomDefaultValueSeparatorMapper.class);
    SupportClasses.CustomCache cache = SupportClasses.Utils.unwrap(configuration.getCache(CustomDefaultValueSeparatorMapper.class.getName()));
    Assertions.assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.NULL);
    Assertions.assertThat(((UnpooledDataSource) configuration.getEnvironment().getDataSource()).getUrl()).isEqualTo("jdbc:hsqldb:mem:global_variables_defaults");
    Assertions.assertThat(configuration.getDatabaseId()).isEqualTo("hsql");
    Assertions.assertThat(((SupportClasses.CustomObjectFactory) configuration.getObjectFactory()).getProperties().getProperty("name")).isEqualTo("default");
    Assertions.assertThat(cache.getName()).isEqualTo("default");
    try (SqlSession sqlSession = factory.openSession()) {
      CustomDefaultValueSeparatorMapper mapper = sqlSession.getMapper(CustomDefaultValueSeparatorMapper.class);
      Assertions.assertThat(mapper.selectValue(null)).isEqualTo("default");
    }
  }

  @Test
  void applyPropertyValueWhenCustomizeDefaultValueSeparator() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty(PropertyParser.KEY_DEFAULT_VALUE_SEPARATOR, "?:");
    props.setProperty("settings:jdbcTypeForNull", JdbcType.CHAR.name());
    props.setProperty("db:name", "global_variables_defaults_custom");
    props.setProperty("productName:hsql", "Hsql");
    props.setProperty("objectFactory:name", "customObjectFactory");
    props.setProperty("cache:name", "customCache");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config-custom-separator.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();
    configuration.addMapper(CustomDefaultValueSeparatorMapper.class);
    SupportClasses.CustomCache cache = SupportClasses.Utils.unwrap(configuration.getCache(CustomDefaultValueSeparatorMapper.class.getName()));
    Assertions.assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.CHAR);
    Assertions.assertThat(((UnpooledDataSource) configuration.getEnvironment().getDataSource()).getUrl()).isEqualTo("jdbc:hsqldb:mem:global_variables_defaults_custom");
    Assertions.assertThat(configuration.getDatabaseId()).isNull();
    Assertions.assertThat(((SupportClasses.CustomObjectFactory) configuration.getObjectFactory()).getProperties().getProperty("name")).isEqualTo("customObjectFactory");
    Assertions.assertThat(cache.getName()).isEqualTo("customCache");
    try (SqlSession sqlSession = factory.openSession()) {
      CustomDefaultValueSeparatorMapper mapper = sqlSession.getMapper(CustomDefaultValueSeparatorMapper.class);
      Assertions.assertThat(mapper.selectValue("3333")).isEqualTo("3333");
    }
  }

  @CacheNamespace(implementation = SupportClasses.CustomCache.class, properties = {
      @Property(name = "name", value = "${cache:name?:default}")
  })
  private interface CustomDefaultValueSeparatorMapper {
    @Select("SELECT '${val != null ? val : 'default'}' FROM INFORMATION_SCHEMA.SYSTEM_USERS")
    String selectValue(@Param("val") String val);
  }
}
