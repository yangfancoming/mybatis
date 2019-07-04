
package org.apache.ibatis.submitted.global_variables_defaults;

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

public class XmlMapperTest {

  @Test
  void applyDefaultValueOnXmlMapper() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();
    configuration.addMapper(XmlMapper.class);
    SupportClasses.CustomCache cache = SupportClasses.Utils.unwrap(configuration.getCache(XmlMapper.class.getName()));

    Assertions.assertThat(cache.getName()).isEqualTo("default");

    try (SqlSession sqlSession = factory.openSession()) {
      XmlMapper mapper = sqlSession.getMapper(XmlMapper.class);

      Assertions.assertThat(mapper.ping()).isEqualTo("Hello");
      Assertions.assertThat(mapper.selectOne()).isEqualTo("1");
      Assertions.assertThat(mapper.selectFromVariable()).isEqualTo("9999");
    }

  }

  @Test
  void applyPropertyValueOnXmlMapper() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("ping.sql", "SELECT 'Hi' FROM INFORMATION_SCHEMA.SYSTEM_USERS");
    props.setProperty("cache.name", "custom");
    props.setProperty("select.columns", "'5555'");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();
    configuration.addMapper(XmlMapper.class);
    SupportClasses.CustomCache cache = SupportClasses.Utils.unwrap(configuration.getCache(XmlMapper.class.getName()));

    Assertions.assertThat(cache.getName()).isEqualTo("custom");

    try (SqlSession sqlSession = factory.openSession()) {
      XmlMapper mapper = sqlSession.getMapper(XmlMapper.class);

      Assertions.assertThat(mapper.ping()).isEqualTo("Hi");
      Assertions.assertThat(mapper.selectOne()).isEqualTo("1");
      Assertions.assertThat(mapper.selectFromVariable()).isEqualTo("5555");
    }

  }

  public interface XmlMapper {

    String ping();

    String selectOne();

    String selectFromVariable();

  }

}
