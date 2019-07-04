
package org.apache.ibatis.submitted.global_variables_defaults;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class AnnotationMapperTest {

  @Test
  public void applyDefaultValueOnAnnotationMapper() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();
    configuration.addMapper(AnnotationMapper.class);
    SupportClasses.CustomCache cache = SupportClasses.Utils.unwrap(configuration.getCache(AnnotationMapper.class.getName()));

    Assertions.assertThat(cache.getName()).isEqualTo("default");

    try (SqlSession sqlSession = factory.openSession()) {
      AnnotationMapper mapper = sqlSession.getMapper(AnnotationMapper.class);

      Assertions.assertThat(mapper.ping()).isEqualTo("Hello");
    }

  }

  @Test
  public void applyPropertyValueOnAnnotationMapper() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("ping.sql", "SELECT 'Hi' FROM INFORMATION_SCHEMA.SYSTEM_USERS");
    props.setProperty("cache.name", "custom");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();
    configuration.addMapper(AnnotationMapper.class);
    SupportClasses.CustomCache cache = SupportClasses.Utils.unwrap(configuration.getCache(AnnotationMapper.class.getName()));

    Assertions.assertThat(cache.getName()).isEqualTo("custom");

    try (SqlSession sqlSession = factory.openSession()) {
      AnnotationMapper mapper = sqlSession.getMapper(AnnotationMapper.class);

      Assertions.assertThat(mapper.ping()).isEqualTo("Hi");
    }

  }

  @CacheNamespace(implementation = SupportClasses.CustomCache.class, properties = {
      @Property(name = "name", value = "${cache.name:default}")
  })
  public interface AnnotationMapper {

    @Select("${ping.sql:SELECT 'Hello' FROM INFORMATION_SCHEMA.SYSTEM_USERS}")
    String ping();

  }

}
