
package org.apache.ibatis.builder;

import org.apache.common.MyBaseXmlConfig;
import org.apache.ibatis.builder.mapper.CustomMapper;
import org.apache.ibatis.builder.typehandler.CustomIntegerTypeHandler;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Blog;
import org.apache.ibatis.domain.blog.mappers.BlogMapper;
import org.apache.ibatis.domain.blog.mappers.NestedBlogMapper;
import org.apache.ibatis.domain.jpetstore.Cart;
import org.apache.ibatis.executor.loader.cglib.CglibProxyFactory;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.io.JBoss6VFS;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class XmlConfigBuilderTest extends MyBaseXmlConfig {

  @Test
  void shouldSuccessfullyLoadMinimalXMLConfigFile() throws IOException {
    Configuration config = getConfiguration("org/apache/ibatis/builder/MinimalMapperConfig.xml");
    assertEquals(AutoMappingBehavior.PARTIAL,config.getAutoMappingBehavior());
    assertEquals(AutoMappingUnknownColumnBehavior.NONE,config.getAutoMappingUnknownColumnBehavior());
    assertEquals(true,config.cacheEnabled);

    assertThat(config.getProxyFactory()).isInstanceOf(JavassistProxyFactory.class);
    assertEquals(false,config.isLazyLoadingEnabled());

    assertThat(config.isAggressiveLazyLoading()).isFalse();
    assertThat(config.isMultipleResultSetsEnabled()).isTrue();
    assertThat(config.isUseColumnLabel()).isTrue();
    assertThat(config.isUseGeneratedKeys()).isFalse();
    assertThat(config.getDefaultExecutorType()).isEqualTo(ExecutorType.SIMPLE);
    assertNull(config.getDefaultStatementTimeout());
    assertNull(config.getDefaultFetchSize());
    assertThat(config.isMapUnderscoreToCamelCase()).isFalse();
    assertThat(config.isSafeRowBoundsEnabled()).isFalse();
    assertEquals(LocalCacheScope.SESSION,config.getLocalCacheScope());

    assertThat(config.getJdbcTypeForNull()).isEqualTo(JdbcType.OTHER);
    assertThat(config.getLazyLoadTriggerMethods()).isEqualTo(new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString")));
    assertThat(config.isSafeResultHandlerEnabled()).isTrue();
    assertThat(config.getDefaultScriptingLanuageInstance()).isInstanceOf(XMLLanguageDriver.class);
    assertEquals(false,config.isCallSettersOnNulls());
    assertNull(config.getLogPrefix());
    assertNull(config.getLogImpl());
    assertNull(config.getConfigurationFactory());
    assertThat(config.getTypeHandlerRegistry().getTypeHandler(RoundingMode.class)).isInstanceOf(EnumTypeHandler.class);
  }

  @Test
  void registerJavaTypeInitializingTypeHandler() throws Exception {
    XMLConfigBuilder builder = getXMLConfigBuilder("org/apache/ibatis/builder/RegisterJavaTypeInitializingTypeHandler.xml");
    builder.parse();
    TypeHandlerRegistry typeHandlerRegistry = builder.getConfiguration().getTypeHandlerRegistry();
    TypeHandler<MyEnum> typeHandler = typeHandlerRegistry.getTypeHandler(MyEnum.class);
    assertTrue(typeHandler instanceof EnumOrderTypeHandler);
    assertArrayEquals(MyEnum.values(), ((EnumOrderTypeHandler<MyEnum>) typeHandler).constants);
  }

  @Test
  void shouldSuccessfullyLoadXMLConfigFile() throws Exception {
    String resource = "org/apache/ibatis/builder/CustomizedSettingsMapperConfig.xml";
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
      Properties props = new Properties();
      props.put("prop2", "cccc");
      XMLConfigBuilder builder = new XMLConfigBuilder(inputStream, null, props);
      Configuration config = builder.parse();

      assertThat(config.getAutoMappingBehavior()).isEqualTo(AutoMappingBehavior.NONE);
      assertThat(config.getAutoMappingUnknownColumnBehavior()).isEqualTo(AutoMappingUnknownColumnBehavior.WARNING);
      assertThat(config.cacheEnabled).isFalse();
      assertThat(config.getProxyFactory()).isInstanceOf(CglibProxyFactory.class);
      assertThat(config.isLazyLoadingEnabled()).isTrue();
      assertThat(config.isAggressiveLazyLoading()).isTrue();
      assertThat(config.isMultipleResultSetsEnabled()).isFalse();
      assertThat(config.isUseColumnLabel()).isFalse();
      assertThat(config.isUseGeneratedKeys()).isTrue();
      assertThat(config.getDefaultExecutorType()).isEqualTo(ExecutorType.BATCH);
      assertThat(config.getDefaultStatementTimeout()).isEqualTo(10);
      assertThat(config.getDefaultFetchSize()).isEqualTo(100);
      assertThat(config.isMapUnderscoreToCamelCase()).isTrue();
      assertThat(config.isSafeRowBoundsEnabled()).isTrue();
      assertThat(config.getLocalCacheScope()).isEqualTo(LocalCacheScope.STATEMENT);
      assertThat(config.getJdbcTypeForNull()).isEqualTo(JdbcType.NULL);
      assertThat(config.getLazyLoadTriggerMethods()).isEqualTo(new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString", "xxx")));
      assertThat(config.isSafeResultHandlerEnabled()).isFalse();
      assertThat(config.getDefaultScriptingLanuageInstance()).isInstanceOf(RawLanguageDriver.class);
      assertThat(config.isCallSettersOnNulls()).isTrue();
      assertThat(config.getLogPrefix()).isEqualTo("mybatis_");
      assertThat(config.getLogImpl().getName()).isEqualTo(Slf4jImpl.class.getName());
      assertThat(config.getVfsImpl().getName()).isEqualTo(JBoss6VFS.class.getName());
      assertThat(config.getConfigurationFactory().getName()).isEqualTo(String.class.getName());

      assertThat(config.getTypeAliasRegistry().getTypeAliases().get("blogauthor")).isEqualTo(Author.class);
      assertThat(config.getTypeAliasRegistry().getTypeAliases().get("blog")).isEqualTo(Blog.class);
      assertThat(config.getTypeAliasRegistry().getTypeAliases().get("cart")).isEqualTo(Cart.class);

      assertThat(config.getTypeHandlerRegistry().getTypeHandler(Integer.class)).isInstanceOf(CustomIntegerTypeHandler.class);
      assertThat(config.getTypeHandlerRegistry().getTypeHandler(Long.class)).isInstanceOf(CustomLongTypeHandler.class);
      assertThat(config.getTypeHandlerRegistry().getTypeHandler(String.class)).isInstanceOf(CustomStringTypeHandler.class);
      assertThat(config.getTypeHandlerRegistry().getTypeHandler(String.class, JdbcType.VARCHAR)).isInstanceOf(CustomStringTypeHandler.class);
      assertThat(config.getTypeHandlerRegistry().getTypeHandler(RoundingMode.class)).isInstanceOf(EnumOrdinalTypeHandler.class);

      ExampleObjectFactory objectFactory = (ExampleObjectFactory) config.getObjectFactory();
      assertThat(objectFactory.getProperties().size()).isEqualTo(1);
      assertThat(objectFactory.getProperties().getProperty("objectFactoryProperty")).isEqualTo("100");

      assertThat(config.getObjectWrapperFactory()).isInstanceOf(CustomObjectWrapperFactory.class);

      assertThat(config.getReflectorFactory()).isInstanceOf(CustomReflectorFactory.class);

      ExamplePlugin plugin = (ExamplePlugin) config.getInterceptors().get(0);
      assertThat(plugin.getProperties().size()).isEqualTo(1);
      assertThat(plugin.getProperties().getProperty("pluginProperty")).isEqualTo("100");

      Environment environment = config.getEnvironment();
      assertThat(environment.getId()).isEqualTo("development");
      assertThat(environment.getDataSource()).isInstanceOf(UnpooledDataSource.class);
      assertThat(environment.getTransactionFactory()).isInstanceOf(JdbcTransactionFactory.class);

      assertThat(config.databaseId).isEqualTo("derby");

      assertThat(config.getMapperRegistry().getMappers().size()).isEqualTo(4);
      assertThat(config.getMapperRegistry().hasMapper(CachedAuthorMapper.class)).isTrue();
      assertThat(config.getMapperRegistry().hasMapper(CustomMapper.class)).isTrue();
      assertThat(config.getMapperRegistry().hasMapper(BlogMapper.class)).isTrue();
      assertThat(config.getMapperRegistry().hasMapper(NestedBlogMapper.class)).isTrue();
    }
  }

  @Test  //  遍历 <properties> 标签   jdbc.properties 中4个属性  + 自定义3个属性 共遍历出7个
  void shouldSuccessfullyLoadXMLConfigFileWithPropertiesUrl() throws Exception {
    Configuration config = getConfiguration("org/apache/ibatis/builder/PropertiesUrlMapperConfig.xml");
    Properties variables = config.getVariables();
    variables.entrySet().stream().forEach(x->System.out.println(x.getKey()+ "---"+x.getValue()));
    assertEquals(7,variables.size());
  }

  @Test // 全局配置文件只能被解析一次
  void parseIsTwice() throws Exception {
    String resource = "org/apache/ibatis/builder/MinimalMapperConfig.xml";
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
      XMLConfigBuilder builder = new XMLConfigBuilder(inputStream);
      builder.parse();
      when(builder).parse();
      then(caughtException()).isInstanceOf(BuilderException.class).hasMessage("Each XMLConfigBuilder can only be used once.");
    }
  }

  @Test
  void unknownSettings() throws Exception {
    XMLConfigBuilder builder = getXMLConfigBuilder("org/apache/ibatis/builder/UnknownSettings.xml");
    when(builder).parse();
    then(caughtException()).isInstanceOf(BuilderException.class).hasMessageContaining("The setting foo is not known.  Make sure you spelled it correctly (case sensitive).");
  }

  @Test
  void knownSettings() throws Exception {
    XMLConfigBuilder builder = getXMLConfigBuilder("org/apache/ibatis/builder/KnownSettings.xml");
    Configuration config = builder.parse();
    assertNotNull(config);
  }

  @Test
  void unknownJavaTypeOnTypeHandler() throws Exception {
    XMLConfigBuilder builder = getXMLConfigBuilder("org/apache/ibatis/builder/UnknownJavaTypeOnTypeHandler.xml");
    when(builder).parse();
    then(caughtException()).isInstanceOf(BuilderException.class).hasMessageContaining("Error registering typeAlias for 'null'. Cause: ");
  }

  @Test
  void propertiesSpecifyResourceAndUrlAtSameTime() throws Exception {
    XMLConfigBuilder builder = getXMLConfigBuilder("org/apache/ibatis/builder/PropertiesSpecifyResourceAndUrlAtSameTime.xml");
    when(builder).parse();
    then(caughtException()).isInstanceOf(BuilderException.class).hasMessageContaining("The properties element cannot specify both a URL and a resource based property file reference. Please specify one or the other.");
  }

  enum MyEnum {
    ONE, TWO
  }

  public static class EnumOrderTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private E[] constants;

    public EnumOrderTypeHandler(Class<E> javaType) {
      constants = javaType.getEnumConstants();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
      ps.setInt(i, parameter.ordinal() + 1); // 0 means NULL so add +1
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
      int index = rs.getInt(columnName) - 1;
      return index < 0 ? null : constants[index];
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
      int index = rs.getInt(rs.getInt(columnIndex)) - 1;
      return index < 0 ? null : constants[index];
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
      int index = cs.getInt(columnIndex) - 1;
      return index < 0 ? null : constants[index];
    }
  }
}
