
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
   *  当创建XMLConfigBuilder对象时，就会初始化Configuration对象，
   *  并且在初始化Configuration对象的时候，一些别名会被注册到Configuration的 typeAliasRegistry 容器中
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
    //1.判断是否已经解析过，不重复解析 //判断是否已经完成对mybatis-config.xml配置文件的解析
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
    //2.读取主配置文件的configuration节点下面的配置信息，parseConfiguration方法完成解析的流程
    parseConfiguration(xNode);
    return configuration;
  }

  /**
   * 解析核心配置文件的关键方法，
   * 读取节点的信息，并通过对应的方法去解析配置，解析到的配置全部会放在configuration里面
   * */
  private void parseConfiguration(XNode root) {
    try {
      //issue #117 read properties first // 解析<properties>节点
      propertiesElement(root.evalNode("properties"));
      // 解析<settings>节点 并将其转换为 Properties 对象。 <settings>属性的解析过程和 <properties>属性的解析过程极为类似，这里不再赘述。最终，所有的setting属性都被存储在Configuration对象中。
      Properties settings = settingsAsProperties(root.evalNode("settings"));
      // 加载 vfs
      loadCustomVfs(settings);
      // 加载自定义日志  日志加载配置
      loadCustomLogImpl(settings);
      // 解析<typeAliases>节点
      typeAliasesElement(root.evalNode("typeAliases"));
      // 解析<plugins>节点
      pluginElement(root.evalNode("plugins"));
      // 解析 objectFactory 配置  mybatis为结果创建对象时都会用到objectFactory
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
      // MyBatis能够执行不同的语句取决于你提供的数据库供应商。许多数据库供应商的支持是基于databaseId映射
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));
      // 解析 typeHandlers 配置  当MyBatis设置参数到PreparedStatement 或者从ResultSet 结果集中取得值时，就会使用TypeHandler  来处理数据库类型与java 类型之间转换
      typeHandlerElement(root.evalNode("typeHandlers"));
      // 解析<mappers>节点  主要的crud操作都是在mappers中定义的
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

  /**
    从 MyBatis 的 TypeAliasRegistry 中查找 logImpl 键所对应值的类对象
    这里 logImpl 对应的 value 值可以从 org.apache.ibatis.session.Configuration 的构造方法中找到
    注意 Log 类，这是 MyBatis 内部对日志对象的抽象

   <settings>
     <setting name="logImpl" value="NO_LOGGING"/>
   </settings>
  */
  private void loadCustomLogImpl(Properties props) {
    Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
    // 将查找到的 Class 对象设置到 Configuration 对象中
    configuration.setLogImpl(logImpl);
  }

/**
 如果<typeAliases>节点下定义了<package>节点，那么MyBatis会给该包下的所有类起一个别名（以类名首字母小写作为别名）
 如果<typeAliases>节点下定义了<typeAlias>节点，那么MyBatis就会给指定的类起指定的别名。
 这些别名都会被存入configuration的typeAliasRegistry容器中。
*/
  private void typeAliasesElement(XNode parent) {
    if (parent != null) {
      // //1.非空才会处理，依次遍历所有节点 遍历<typeAliases>下的所有子节点
      for (XNode child : parent.getChildren()) {
        //2.处理package类型配置 // 若当前结点为<package>
        if ("package".equals(child.getName())) {
          //  //2.1获取包名 获取<package>上的name属性（包名）
          String typeAliasPackage = child.getStringAttribute("name");
          // 为该包下的所有类起个别名，并注册进configuration的typeAliasRegistry中
          //2.2注册包名，将包名放到typeAliasRegistry里面，里面拿到包名之后还会进一步处理 最后会放到TypeAliasRegistry.TYPE_ALIASES这个Map里面去
          configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
        } else {
          //3.处理typeAlias类型配置  // 如果当前结点为< typeAlias >
          //3.1获取别名 // 获取alias和type属性
          String alias = child.getStringAttribute("alias");
          //3.2获取类名
          String type = child.getStringAttribute("type");
          // 注册进configuration的typeAliasRegistry中
          try {
            Class<?> clazz = Resources.classForName(type);
            //3.3下面的注册逻辑其实比前面注册包名要简单，注册包名要依次处理包下的类，也会调用registerAlias方法，
            //这里直接处理类，别名没有配置也没关系，里面会生成一个getSimpleName或者根据Alias注解去取别名
            if (alias == null) {
              typeAliasRegistry.registerAlias(clazz);
            } else {
              typeAliasRegistry.registerAlias(alias, clazz);
            }
          } catch (ClassNotFoundException e) {
            //4.其他类型直接报错
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


   <mappers>
   <!--直接映射到相应的mapper文件 -->
   <mapper resource="mybatis/mapper/EmployeeMapper.xml"/>
   <mapper url="xx"/>
   <mapper class="yy"/>
   <package name="com.intellif.mozping"/>
   </mappers>

   * 解析配置文件的mappers子节点，方法主要是实现一个大体框架，按照resource->url->class的优先级读取配置，具体的解
   * 析细节是依赖于XMLMapperBuilder来实现的，XMLMapperBuilder通过parse方法屏蔽了细节，内部完成解析过程
   */
  private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
      //1.节点非空，遍历子节点逐个处理，因为mapperElement(root.evalNode("mappers"))解析的mappers里面可能有多个标签 // 遍历<mappers>下所有子节点
      for (XNode child : parent.getChildren()) {
        // 如果当前节点为<package>
        if ("package".equals(child.getName())) {
          //1.1 处理package类型的配置 // 获取<package>的name属性（该属性值为mapper class所在的包名）
          String mapperPackage = child.getStringAttribute("name");
          //1.2 按照包来添加，扫包之后默认会在包下找与java接口名称相同的mapper映射文件，name就是包名， // 将该包下的所有Mapper Class注册到configuration的mapperRegistry容器中
          configuration.addMappers(mapperPackage);
        } else {
          /** 如果当前节点为<mapper> 依次获取resource、url、class属性
           * mapper节点配置有3个属性：resource,url,class。他们处理的优先级依次是resource,url,class，3个属性只处理一种
           * 1.3 一个一个Mapper.xml文件的添加 ， resource、url和class三者是互斥的，resource优先级最高
          */
          String resource = child.getStringAttribute("resource");
          String url = child.getStringAttribute("url");
          String mapperClass = child.getStringAttribute("class");
          //1.4 按照resource属性实例化XMLMapperBuilder来解析xml配置文件
          if (resource != null && url == null && mapperClass == null) { // 解析resource属性（Mapper.xml文件的路径）  <mapper resource="com/dy/dao/userDao.xml"/>
            ErrorContext.instance().resource(resource);
            InputStream inputStream = Resources.getResourceAsStream(resource); // 将Mapper.xml文件解析成输入流
            // 使用XMLMapperBuilder解析Mapper.xml，并将Mapper Class注册进configuration对象的mapperRegistry容器中
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            //1.5解析配置，因为XMLMapperBuilder继承了BaseBuilder，BaseBuilder内部持有Configuration对象，因此
            //XMLMapperBuilder解析之后直接把配置设置到Configuration对象
            mapperParser.parse();
          } else if (resource == null && url != null && mapperClass == null) {  // 解析url属性（Mapper.xml文件的路径） <mapper url="file://........"/>
            //1.6 按照url属性实例化XMLMapperBuilder来解析xml配置文件
            ErrorContext.instance().resource(url);
            InputStream inputStream = Resources.getUrlAsStream(url);
            // 通过读取resource或url属性得到xml的访问路径后，交给XMLMapperBuilder对象来解析
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url == null && mapperClass != null) { // 解析class属性（Mapper Class的全限定名）  <mapper class="com.dy.dao.UserDao"/>
            //1.7 按照class属性实例化XMLMapperBuilder来解析xml配置文件
            // 将Mapper Class的权限定名转化成Class对象
            Class<?> mapperInterface = Resources.classForName(mapperClass);
            // 注册进configuration对象的mapperRegistry容器中
            configuration.addMapper(mapperInterface);
          } else {
            //resource、url和class三者是互斥的，配置了多个或者不配置都抛出异常
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
