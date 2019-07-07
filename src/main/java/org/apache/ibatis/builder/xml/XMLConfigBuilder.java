
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

/**
 * XMLConfigBuilder 用来解析MyBatis的配置文件  eg: "org/apache/ibatis/submitted/association_nested/mybatis-config.xml"
*/
public class XMLConfigBuilder extends BaseBuilder {
  //标识是否已经解析过mybatis-config.xml配置文件
  private boolean parsed;
  //用于解析mybatis-config.xml配置文件的XPathParser对象
  private final XPathParser parser;
  //标识<enviroment>配置的名称，默认读取<enviroment>标签的default属性
  private String environment;
  //反射工厂，用于创建和缓存反射对象
  private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

  public XMLConfigBuilder(Reader reader) {
    this(reader, null, null);
  }

  public XMLConfigBuilder(Reader reader, String environment) {
    this(reader, environment, null);
  }

  public XMLConfigBuilder(Reader reader, String environment, Properties props) {
    this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  public XMLConfigBuilder(InputStream inputStream) {
    this(inputStream, null, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment) {
    this(inputStream, environment, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
    this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  /**
   *  当创建XMLConfigBuilder对象时，就会初始化Configuration对象，并且在初始化Configuration对象的时候，一些别名会被注册到Configuration的typeAliasRegistry容器中
  */
  private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
    super(new Configuration());
    ErrorContext.instance().resource("SQL Mapper Configuration");
    this.configuration.setVariables(props);
    this.parsed = false;
    this.environment = environment;
    this.parser = parser;
  }

  public Configuration parse() {
    //判断是否已经完成对mybatis-config.xml配置文件的解析
    if (parsed) {
      throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    }
    parsed = true;
    /**  解析 xml 配置文件
     注意一个 xpath 表达式 /configuration 这个表达式 代表的是 MyBatis 配置文件的 <configuration> 节点
     这里通过 xpath 选中这个节点，并传 递给 parseConfiguration 方法
     即： 在mybatis-config.xml配置文件中查找<configuration>节点，并开始解析
    */
    XNode xNode = parser.evalNode("/configuration");
    parseConfiguration(xNode);
    return configuration;
  }

  private void parseConfiguration(XNode root) {
    try {
      //issue #117 read properties first // 解析<properties>节点
      propertiesElement(root.evalNode("properties"));
      // 解析<settings>节点 并将其转换为 Properties 对象。 <settings>属性的解析过程和 <properties>属性的解析过程极为类似，这里不再赘述。最终，所有的setting属性都被存储在Configuration对象中。
      Properties settings = settingsAsProperties(root.evalNode("settings"));
      // 加载 vfs
      loadCustomVfs(settings);
      loadCustomLogImpl(settings);
      // 解析<typeAliases>节点
      typeAliasesElement(root.evalNode("typeAliases"));
      // 解析<plugins>节点
      pluginElement(root.evalNode("plugins"));
      // 解析 objectFactory 配置
      objectFactoryElement(root.evalNode("objectFactory"));
      // 解析 objectWrapperFactory 配置
      objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
      // 解析<reflectorFactory>节点
      reflectorFactoryElement(root.evalNode("reflectorFactory"));
      // settings 中的信息设置到 Configuration 对象中
      settingsElement(settings);
      // read it after objectFactory and objectWrapperFactory issue #631  // 解析<environments>节点
      environmentsElement(root.evalNode("environments"));
      // 解析 databaseIdProvider，获取并设置 databaseId 到 Configuration 对象
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));
      // 解析 typeHandlers 配置
      typeHandlerElement(root.evalNode("typeHandlers"));
      // 解析<mappers>节点
      mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
  }

  /**
   1. 解析 settings 子节点的内容，并将解析结果转成 Properties 对象
   2. 为 Configuration 创建元信息对象
   3. 通过 MetaClass 检测 Configuration 中是否存在某个属性的 setter 方法，不存在则抛异常
   4. 若通过 MetaClass 的检测，则返回 Properties 对象，方法逻辑结束
  */
  private Properties settingsAsProperties(XNode context) {
    if (context == null) {
      return new Properties();
    }
    // 获取 settings 子节点中的内容
    Properties props = context.getChildrenAsProperties();
    // Check that all settings are known to the configuration class
    // 创建 Configuration 类的“元信息”对象
    MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
    for (Object key : props.keySet()) {
      // 检测 Configuration 中是否存在相关属性，不存在则抛出异常
      if (!metaConfig.hasSetter(String.valueOf(key))) {
        throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
      }
    }
    return props;
  }

  private void loadCustomVfs(Properties props) throws ClassNotFoundException {
    String value = props.getProperty("vfsImpl");
    if (value != null) {
      String[] clazzes = value.split(",");
      for (String clazz : clazzes) {
        if (!clazz.isEmpty()) {
          @SuppressWarnings("unchecked")
          Class<? extends VFS> vfsImpl = (Class<? extends VFS>)Resources.classForName(clazz);
          configuration.setVfsImpl(vfsImpl);
        }
      }
    }
  }

  private void loadCustomLogImpl(Properties props) {
    Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
    configuration.setLogImpl(logImpl);
  }

/**
 如果<typeAliases>节点下定义了<package>节点，那么MyBatis会给该包下的所有类起一个别名（以类名首字母小写作为别名）
 如果<typeAliases>节点下定义了<typeAlias>节点，那么MyBatis就会给指定的类起指定的别名。
 这些别名都会被存入configuration的typeAliasRegistry容器中。
*/
  private void typeAliasesElement(XNode parent) {
    if (parent != null) {
      // 遍历<typeAliases>下的所有子节点
      for (XNode child : parent.getChildren()) {
        // 若当前结点为<package>
        if ("package".equals(child.getName())) {
          // 获取<package>上的name属性（包名）
          String typeAliasPackage = child.getStringAttribute("name");
          // 为该包下的所有类起个别名，并注册进configuration的typeAliasRegistry中
          configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
        } else { // 如果当前结点为< typeAlias >
          // 获取alias和type属性
          String alias = child.getStringAttribute("alias");
          String type = child.getStringAttribute("type");
          // 注册进configuration的typeAliasRegistry中
          try {
            Class<?> clazz = Resources.classForName(type);
            if (alias == null) {
              typeAliasRegistry.registerAlias(clazz);
            } else {
              typeAliasRegistry.registerAlias(alias, clazz);
            }
          } catch (ClassNotFoundException e) {
            throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
          }
        }
      }
    }
  }

  private void pluginElement(XNode parent) throws Exception {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        String interceptor = child.getStringAttribute("interceptor");
        Properties properties = child.getChildrenAsProperties();
        Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
        interceptorInstance.setProperties(properties);
        configuration.addInterceptor(interceptorInstance);
      }
    }
  }

  private void objectFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties properties = context.getChildrenAsProperties();
      ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
      factory.setProperties(properties);
      configuration.setObjectFactory(factory);
    }
  }

  private void objectWrapperFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).newInstance();
      configuration.setObjectWrapperFactory(factory);
    }
  }

  private void reflectorFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      ReflectorFactory factory = (ReflectorFactory) resolveClass(type).newInstance();
      configuration.setReflectorFactory(factory);
    }
  }

  /**
   首先读取<resources>节点下的所有<resource>节点，并将每个节点的name和value属性存入Properties中。
   然后读取<resources>节点上的resource、url属性，并获取指定配置文件中的name和value，也存入Properties中。
   （PS：由此可知，如果resource节点上定义的属性和properties文件中的属性重名，那么properties文件中的属性值会覆盖resource节点上定义的属性值。）
   最终，携带所有属性的Properties对象会被存储在Configuration对象中。

   <properties>节点解析过程，不是很复杂。主要包含三个步骤，
   一是解析 <properties>节点的子节点，并将解析结果设置到 Properties 对象中。
   二是从文件系统或通过网络读取属性配置，这取决于<properties>节点的 resource 和 url 是否为空。
   最后一步则是将包含属性信息的 Properties 对象设置到
   XPathParser 和 Configuration 中
  */
  private void propertiesElement(XNode context) throws Exception {
    if (context != null) {
      // 获取<properties>节点的所有子节点 并将这些节点内容转换为属性对象 Properties
      Properties defaults = context.getChildrenAsProperties();
      // 获取<properties>节点上的resource属性
      String resource = context.getStringAttribute("resource");
      // 获取<properties>节点上的url属性
      String url = context.getStringAttribute("url");
      // resource和url不能同时存在
      if (resource != null && url != null) {
        throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
      }
      if (resource != null) {
        // 获取resource属性值对应的properties文件中的键值对，并添加至defaults容器中
        defaults.putAll(Resources.getResourceAsProperties(resource));
      } else if (url != null) {
        // 获取url属性值对应的properties文件中的键值对，并添加至defaults容器中
        defaults.putAll(Resources.getUrlAsProperties(url));
      }
      // 获取configuration中原本的属性，并添加至defaults容器中
      Properties vars = configuration.getVariables();
      if (vars != null) {
        defaults.putAll(vars);
      }
      parser.setVariables(defaults);
      // 将defaults容器添加至configuration中
      configuration.setVariables(defaults);
    }
  }

  private void settingsElement(Properties props) {
    // 设置 autoMappingBehavior 属性，默认值为 PARTIAL
    configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
    configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
    // 设置 cacheEnabled 属性，默认值为 true
    configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
    configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
    configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
    configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), false));
    configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
    configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
    configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
    configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
    configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
    configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
    configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
    configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
    configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
    configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
    configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
    configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
    configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
    // 解析默认的枚举处理器
    configuration.setDefaultEnumTypeHandler(resolveClass(props.getProperty("defaultEnumTypeHandler")));
    configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
    configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
    configuration.setReturnInstanceForEmptyRow(booleanValueOf(props.getProperty("returnInstanceForEmptyRow"), false));
    configuration.setLogPrefix(props.getProperty("logPrefix"));
    configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
  }

  /**
   <environments default="development">
     <environment id="development">
       <transactionManager type="JDBC"/>
         <dataSource type="POOLED">
             <property name="driver" value="org.hsqldb.jdbc.JDBCDriver"/>
             <property name="url" value="jdbc:hsqldb:mem:association_nested"/>
             <property name="username" value="SA"/>
             <property name="password" value=""/>
         </dataSource>
     </environment>
   </environments>
  */
  private void environmentsElement(XNode context) throws Exception {
    if (context != null) {
      if (environment == null) {
        environment = context.getStringAttribute("default");
      }
      for (XNode child : context.getChildren()) {
        String id = child.getStringAttribute("id");
        if (isSpecifiedEnvironment(id)) {
          TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
          DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
          DataSource dataSource = dsFactory.getDataSource();
          Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory).dataSource(dataSource);
          configuration.setEnvironment(environmentBuilder.build());
        }
      }
    }
  }

  private void databaseIdProviderElement(XNode context) throws Exception {
    DatabaseIdProvider databaseIdProvider = null;
    if (context != null) {
      String type = context.getStringAttribute("type");
      // awful patch to keep backward compatibility
      if ("VENDOR".equals(type)) {
        type = "DB_VENDOR";
      }
      Properties properties = context.getChildrenAsProperties();
      databaseIdProvider = (DatabaseIdProvider) resolveClass(type).newInstance();
      databaseIdProvider.setProperties(properties);
    }
    Environment environment = configuration.getEnvironment();
    if (environment != null && databaseIdProvider != null) {
      String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
      configuration.setDatabaseId(databaseId);
    }
  }

  private TransactionFactory transactionManagerElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties props = context.getChildrenAsProperties();
      TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a TransactionFactory.");
  }

  // 获取 DataSource 四大金刚元素
  private DataSourceFactory dataSourceElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties props = context.getChildrenAsProperties();
      // 通过 TypeAliasRegistry 中的  Map<String, Class<?>> typeAliases 中的key  获取 org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
      DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a DataSourceFactory.");
  }

  private void typeHandlerElement(XNode parent) {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        if ("package".equals(child.getName())) {
          String typeHandlerPackage = child.getStringAttribute("name");
          typeHandlerRegistry.register(typeHandlerPackage);
        } else {
          String javaTypeName = child.getStringAttribute("javaType");
          String jdbcTypeName = child.getStringAttribute("jdbcType");
          String handlerTypeName = child.getStringAttribute("handler");
          Class<?> javaTypeClass = resolveClass(javaTypeName);
          JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
          Class<?> typeHandlerClass = resolveClass(handlerTypeName);
          if (javaTypeClass != null) {
            if (jdbcType == null) {
              typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
            } else {
              typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
            }
          } else {
            typeHandlerRegistry.register(typeHandlerClass);
          }
        }
      }
    }
  }

  /**
   方式1：
   <mappers>
   <package name="org.mybatis.builder"/>
   </mappers>

   方式2：
   <mappers>
   <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
   </mappers>

   方式3：
   <mappers>
   <mapper url="file:///var/mappers/AuthorMapper.xml"/>
   </mappers>

   方式4：
   <mappers>
   <mapper class="org.mybatis.builder.AuthorMapper"/>
   </mappers>
  */
  private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
      // 遍历<mappers>下所有子节点
      for (XNode child : parent.getChildren()) {
        // 如果当前节点为<package>
        if ("package".equals(child.getName())) {
          // 获取<package>的name属性（该属性值为mapper class所在的包名）
          String mapperPackage = child.getStringAttribute("name");
          // 将该包下的所有Mapper Class注册到configuration的mapperRegistry容器中
          configuration.addMappers(mapperPackage);
        } else {  // 如果当前节点为<mapper>
          // 依次获取resource、url、class属性
          String resource = child.getStringAttribute("resource");
          String url = child.getStringAttribute("url");
          String mapperClass = child.getStringAttribute("class");
          if (resource != null && url == null && mapperClass == null) { // 解析resource属性（Mapper.xml文件的路径）  <mapper resource="com/dy/dao/userDao.xml"/>
            ErrorContext.instance().resource(resource);
            InputStream inputStream = Resources.getResourceAsStream(resource); // 将Mapper.xml文件解析成输入流
            // 使用XMLMapperBuilder解析Mapper.xml，并将Mapper Class注册进configuration对象的mapperRegistry容器中
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url != null && mapperClass == null) {  // 解析url属性（Mapper.xml文件的路径） <mapper url="file://........"/>
            ErrorContext.instance().resource(url);
            InputStream inputStream = Resources.getUrlAsStream(url);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url == null && mapperClass != null) { // 解析class属性（Mapper Class的全限定名）  <mapper class="com.dy.dao.UserDao"/>
            // 将Mapper Class的权限定名转化成Class对象
            Class<?> mapperInterface = Resources.classForName(mapperClass);
            // 注册进configuration对象的mapperRegistry容器中
            configuration.addMapper(mapperInterface);
          } else {
            throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
          }
        }
      }
    }
  }

  private boolean isSpecifiedEnvironment(String id) {
    if (environment == null) {
      throw new BuilderException("No environment specified.");
    } else if (id == null) {
      throw new BuilderException("Environment requires an id attribute.");
    } else if (environment.equals(id)) {
      return true;
    }
    return false;
  }

}
