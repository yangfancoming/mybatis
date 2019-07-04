
package org.apache.ibatis.submitted.global_variables_defaults;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.JdbcType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

class ConfigurationTest {

  @Test
  void applyDefaultValueOnXmlConfiguration() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();

    Assertions.assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.NULL);
    Assertions.assertThat(((UnpooledDataSource) configuration.getEnvironment().getDataSource()).getUrl())
            .isEqualTo("jdbc:hsqldb:mem:global_variables_defaults");
    Assertions.assertThat(configuration.getDatabaseId()).isEqualTo("hsql");
    Assertions.assertThat(((SupportClasses.CustomObjectFactory) configuration.getObjectFactory()).getProperties().getProperty("name"))
            .isEqualTo("default");

  }

  @Test
  void applyPropertyValueOnXmlConfiguration() throws IOException {

    Properties props = new Properties();
    props.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
    props.setProperty("settings.jdbcTypeForNull", JdbcType.CHAR.name());
    props.setProperty("db.name", "global_variables_defaults_custom");
    props.setProperty("productName.hsql", "Hsql");
    props.setProperty("objectFactory.name", "custom");

    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/global_variables_defaults/mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
    Configuration configuration = factory.getConfiguration();

    Assertions.assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.CHAR);
    Assertions.assertThat(((UnpooledDataSource) configuration.getEnvironment().getDataSource()).getUrl())
            .isEqualTo("jdbc:hsqldb:mem:global_variables_defaults_custom");
    Assertions.assertThat(configuration.getDatabaseId()).isNull();
    Assertions.assertThat(((SupportClasses.CustomObjectFactory) configuration.getObjectFactory()).getProperties().getProperty("name"))
            .isEqualTo("custom");

  }

}
