
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
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
 * XMLConfigBuilder 用来解析MyBatis的全局xml文件  eg: "org/apache/ibatis/submitted/association_nested/mybatis-config.xml"
 */
public class XMLConfigBuilder extends BaseBuilder {

  private static final Log log = LogFactory.getLog(XMLConfigBuilder.class);

  // 标识是否已经解析过mybatis-config.xml配置文件
  private boolean parsed;
  // 用于解析mybatis-config.xml配置文件的XPathParser对象
  private final XPathParser parser;
  // 用于保存 <environments default="development"> 标签的default
  private String environment;
  // 反射工厂，用于创建和缓存反射对象
  private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

  public XMLConfigBuilder(Reader reader) {
    this(reader, null, null);
  }

  public XMLConfigBuilder(Reader reader, String environment) {
    this(reader, environment, null);
  }

  /** sos 构造函数报错
   *  报错：  Constructor call must be the first statement in a constructor
   *  这个问题其实就是  在构造函数写其他构造函数的时候 只能写在代码块的第一行  不能写在第二行及其以后的行
   *  原因：
   *  （1）： super()在第一行的原因就是: 子类有可能访问了父类对象, 比如在构造函数中使用父类对象的成员函数和变量,
   *        在成员初始化使用了父类, 在代码块中使用了父类等,所以为保证在子类可以访问父类对象之前要完成对父类对象的初始化
   *
   * （2）this()在第一行的原因就是: 为保证父类对象初始化的唯一性.
   *  我们假设一种情况, 类B是类A的子类,如果this()可以在构造函数的任意行使用, 那么会出现什么情况呢?
   *  首先程序运行到构造函数B()的第一行,发现没有调用this()和super(), 就自动在第一行补齐了super() ,
   *  完成了对父类对象的初始化,然后返回子类的构造函数继续执行, 当运行到构造函数B()的"this(2) ;"时,
   *  调用B类对象的B(int) 构造函数,在B(int)中, 还会对父类对象再次初始化!
   *  这就造成了对资源的浪费, 当然也有可能造成某些意想不到的结果, 不管怎样,总之是不合理的, 所以this() 不能出现在除第一行以外的其他行!
   *
   * （3）
   * 不能同时出现，是因为this和super都要定义在第一行，所以只能有一个；
   * 那么为什么要定义在第一行呢？先说super，因为子类继承了父类的属性和方法，
   * 所以在先初始化父类的属性和方法，这样子类才可以初始化自己特有的
   * 因为，或者自定义了带参的super，这样就初始化了父类的成员了，所以写了this的构造函数不能再写super了
   * 因为实例化一个对象运行两次super是不安全的。this放在第一行，也是因为要先初始化父类和this代表的构造函数先
   * 因为当前构造函数可能用到那些成员，所以那些成员得要先初始化。
   */
  public XMLConfigBuilder(Reader reader, String environment, Properties props) {
    //    System.out.println(111); //   报错：  Constructor call must be the first statement in a constructor
    //  1. 通过 reader 创建出 Document
    //  2. 创建出 Xpath = XPathFactory.newInstance().newXPath()
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
    this.parsed = false;
    this.parser = parser;
    this.environment = environment;
    this.configuration.setVariables(props);
    log.warn(" 构造函数1736：XPathParser 地址：" + parser);
    log.warn(" 构造函数1736：configuration 地址：" + this.configuration);
  }

  //外部调用此方法对mybatis的全局xml文件进行解析
  public Configuration parse() {
    log.warn("开始解析全局xml配置文件");
    //1.判断是否已经解析过，不重复解析 //判断是否已经完成对mybatis-config.xml配置文件的解析
    if (parsed) {
      throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    }
    parsed = true;
    /**  解析 xml 全局配置文件
     注意一个 xpath 表达式 /configuration 这个表达式 代表的是 MyBatis 全局xml文件的 <configuration> 节点
     这里通过 xpath 选中这个节点，并传 递给 parseConfiguration 方法
     即： 在mybatis-config.xml配置文件中查找<configuration>节点，并开始解析
     */
    XNode xNode = parser.evalNode("/configuration");
    log.warn("开始解析 <configuration> 标签  XNode 地址：" + xNode.hashCode());
    //2.完成全局xml文件下的configuration节点下的所有标签信息  解析全局xml配置文件
    parseConfiguration(xNode);
    return configuration;
  }

  /**
   * 解析核心配置文件的关键方法，
   * 读取节点的信息，并通过对应的方法去解析配置，解析到的配置全部会放在configuration里面
   * <!ELEMENT configuration (properties?, settings?, typeAliases?, typeHandlers?, objectFactory?, objectWrapperFactory?, reflectorFactory?, plugins?, environments?, databaseIdProvider?, mappers?)>
   * */
  private void parseConfiguration(XNode root) {
    try {
      //issue #117 read properties first // 最先解析<properties>节点
      XNode propertiesNode = root.evalNode("properties");
      propertiesElement(propertiesNode);
      // 解析<settings>节点 并将其转换为 Properties 对象。 <settings>属性的解析过程和 <properties>属性的解析过程极为类似。最终，所有的setting属性都被存储在Configuration对象中。
      XNode temp = root.evalNode("settings");
      Properties settings = settingsAsProperties(temp);
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
    /**
     *  Check that all settings are known to the configuration class 检查配置类是否知道所有设置
     *  创建Configuration对应的MetaClass “元信息” 对象，MetaClass之前有说过是判断类实例是否有getter,setter属性的对象
     */
    MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
    /**
     * 解析<Settings>的子节点<Setting>的name和value属性，并返回Properties对象
     *   <settings>
     *     <setting name="mapUnderscoreToCamelCase" value="true"/>
     *     <setting name="cacheEnabled" value="true" />
     *   </settings>
     *
     * "mapUnderscoreToCamelCase" -> "true"
     * "cacheEnabled" -> "true"
     */
    Properties props = context.getChildrenAsProperties();
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
          // 加载自定义的vfs
          configuration.setVfsImpl(vfsImpl);
        }
      }
    }
  }

  /**
   *    <settings>
   *        <setting name="logImpl" value="NO_LOGGING"/>
   *    </settings>
   从 MyBatis 的 TypeAliasRegistry 中查找 logImpl 键所对应值的类对象
   这里 logImpl 对应的 value 值可以从 org.apache.ibatis.session.Configuration 的构造方法中找到
   注意 Log 类，这是 MyBatis 内部对日志对象的抽象
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
    if (parent == null)  return; // -modify-
    log.warn("开始解析 <typeAliases> 标签  XNode 地址：" + parent.hashCode());
    //1.非空才会处理，依次遍历所有节点 遍历<typeAliases>下的所有子节点
    for (XNode child : parent.getChildren()) {
      //2.处理package类型配置 // 若当前结点为<package>
      if ("package".equals(child.getName())) {
        log.warn("发现 <typeAliases> 节点 使用 package 属性方式");
        //  //2.1获取包名 获取<package>上的name属性（包名）
        String typeAliasPackage = child.getStringAttribute("name");
        // 为该包下的所有类起个别名，并注册进configuration的typeAliasRegistry中
        //2.2注册包名，将包名放到typeAliasRegistry里面，里面拿到包名之后还会进一步处理 最后会放到TypeAliasRegistry.TYPE_ALIASES这个Map里面去
        // 这里为啥 不想 下面那样使用  typeAliasRegistry.registerAlias(clazz); 呢？？？doit
        configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage); // org.apache.goat.common
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
  /**
   *   <plugins>
   *     <plugin interceptor="org.apache.goat.chapter200.D10.MyMybatisPlugin">
   *       <property name="username" value="goat"/>
   *       <property name="password" value="123654"/>
   *     </plugin>
   *     <plugin interceptor="org.apache.goat.chapter200.D1public Object pluginAll(Object target) {0.MySecondPlugin"></plugin>
   *   </plugins>
   */
  private void pluginElement(XNode parent) throws Exception {
    if (parent == null) return;  // modify-
    log.warn("开始解析 <plugins> 标签  XNode 地址：" + parent.hashCode());
    // 遍历<plugins>标签  获取<plugin>
    for (XNode child : parent.getChildren()) {
      // 获取 单个 <plugin>标签下的所有 <property>
      Properties properties = child.getChildrenAsProperties();
      // 获取 <plugin>标签中的 interceptor 属性 ：org.apache.goat.chapter200.D10.MyMybatisPlugin
      String interceptor = child.getStringAttribute("interceptor");
      // 根据 插件类的全路径名 通过反射生成拦截器实例
      Class<?> aClass = resolveClass(interceptor);
      Interceptor interceptorInstance = (Interceptor) aClass.newInstance();
      // 调用我们自定义的重写的 setProperties() 方法
      // 再拿<plugin>标签下的所有<property>标签，解析name和value属性成为一个Properties，将Properties设置到拦截器中
      interceptorInstance.setProperties(properties);
      // 最终添加到 configuration 的拦截器链中
      configuration.addInterceptor(interceptorInstance);
    }
  }

  private void objectFactoryElement(XNode context) throws Exception {
    if (context == null) return; // -modify
    log.warn("开始解析 <objectFactory> 标签  XNode 地址：" + context.hashCode());
    String type = context.getStringAttribute("type");
    Properties properties = context.getChildrenAsProperties();
    ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
    factory.setProperties(properties);
    configuration.setObjectFactory(factory);
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
   最后一步则是将包含属性信息的 Properties 对象设置到 XPathParser 和 Configuration 中
   */
  private void propertiesElement(XNode context) throws Exception {
    if (context == null) return; // modify-
    log.warn("开始解析 <properties> 标签  XNode 地址：" + context.hashCode());
    /**
     * 获取<properties>节点的所有子节点 并将这些节点内容转换为属性对象 Properties
     * 情况一：
     *  <properties resource="dbconfig.properties"></properties>
     *   defaults =  size为0
     *
     * 情况二：
     *     <properties resource="dbconfig.properties">
     *         <property name="jdbc.driver" value="1"/>
     *         <property name="jdbc.url" value="2"/>
     *         <property name="jdbc.username" value="3"/>
     *         <property name="jdbc.password" value="4"/>
     *     </properties>
     *   defaults =  size 为 4
     *   "jdbc.url" -> "2"
     *   "jdbc.username" -> "3"
     *   "jdbc.driver" -> "1"
     *   "jdbc.password" -> "4"
    */
    Properties defaults = context.getChildrenAsProperties();
    // 获取<properties>节点上的resource属性  eg: resource="dbconfig.properties"
    String resource = context.getStringAttribute("resource");
    // 获取<properties>节点上的url属性
    String url = context.getStringAttribute("url");
    // resource 和 url 两个属性不能同时存在
    if (resource != null && url != null) {
      throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference. Please specify one or the other.");
    }
    if (resource != null) {
      // 从文件系统中加载并解析属性文件  获取resource属性值对应的properties文件中的键值对
      Properties properties = Resources.getResourceAsProperties(resource);
      // 添加至defaults容器中 会产生覆盖操作 eg: 将文件中的键值对替换掉  xml标签中的对应的value <property name="jdbc.driver" value="1"/>
      defaults.putAll(properties);
    } else if (url != null) {
      // 获取url属性值对应的properties文件中的键值对，并添加至defaults容器中  会产生覆盖操作
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
    log.warn(  " propertiesElement()：解析<properties> 标签完毕 ：" +  defaults);
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
    // 若全局配置 <setting name="defaultExecutorType" value="FUCK"/> 这里会抛出异常 No enum constant org.apache.ibatis.session.ExecutorType.FUCK    1
    String property = props.getProperty("defaultExecutorType", "SIMPLE");
    configuration.setDefaultExecutorType(ExecutorType.valueOf(property));
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

  private void environmentsElement(XNode context) throws Exception {
    if (context == null) return; // modify-
    log.warn("开始解析 <environments> 标签  XNode 地址：" + context.hashCode());
    if (environment == null) {
      // 获取 <environments default="pro_mysql"> 标签中的 default 属性
      environment = context.getStringAttribute("default");
    }
    /**
     * context.getChildren() 获取 <environments default="pro_mysql"> 标签下的所有子标签
     *     <environment id="dev_hsqldb">
     *     <environment id="pro_mysql">
     *     <environment id="test_mysql">
    */
    for (XNode child : context.getChildren()) {
      //  <environment id="pro_mysql">
      String id = child.getStringAttribute("id");
      if (!isSpecifiedEnvironment(id)) continue; // -modify
      // 解析 <transactionManager type="JDBC"/>  标签
      TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
      // 解析 <dataSource>  标签
      XNode dsNode = child.evalNode("dataSource");
      DataSourceFactory dsFactory = dataSourceElement(dsNode);
      DataSource dataSource = dsFactory.getDataSource();
      // 建造者模式
      Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory).dataSource(dataSource);
      configuration.setEnvironment(environmentBuilder.build());
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
      log.warn("开始解析 <transactionManager> 标签  XNode 地址：" + context.hashCode());
      String type = context.getStringAttribute("type");
      // Configuration 的构造函数中初始化了 JdbcTransactionFactory和ManagedTransactionFactory实现类
      TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
      Properties props = context.getChildrenAsProperties();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a TransactionFactory.");
  }

  /**
   *  解析 <dataSource> 标签并获取 DataSource 四大元素
   *
   *          <dataSource type="POOLED">
   *              <property name="driver" value="org.hsqldb.jdbc.JDBCDriver"/>
   *              <property name="url" value="jdbc:hsqldb:mem:association_nested"/>
   *              <property name="username" value="SA"/>
   *              <property name="password" value=""/>
   *          </dataSource>
   */
  private DataSourceFactory dataSourceElement(XNode context) throws Exception {
    if (context != null) {
      log.warn("开始解析 <dataSource> 标签  XNode 地址：" + context.hashCode());
      // 解析 <dataSource type="POOLED"> 标签中的type属性
      String type = context.getStringAttribute("type");
      // 得到 DataSource 四大<property>元素
      Properties props = context.getChildrenAsProperties();
      // 通过 TypeAliasRegistry 中的  Map<String, Class<?>> typeAliases 中的key  获取 org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
      DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a DataSourceFactory.");
  }

  private void typeHandlerElement(XNode parent) {
    if (parent == null) return; // modify-
    log.warn("开始解析 <typeHandlers> 标签  XNode 地址：" + parent.hashCode());
    for (XNode child : parent.getChildren()) {
      //子节点为package时，获取其name属性的值，然后自动扫描package下的自定义typeHandler
      if ("package".equals(child.getName())) {
        String typeHandlerPackage = child.getStringAttribute("name");
        typeHandlerRegistry.register(typeHandlerPackage);
      } else {
        //子节点为typeHandler时， 可以指定javaType属性， 也可以指定jdbcType, 也可两者都指定
        //javaType 是指定java类型
        //jdbcType 是指定jdbc类型（数据库类型： 如varchar）
        String javaTypeName = child.getStringAttribute("javaType");
        String jdbcTypeName = child.getStringAttribute("jdbcType");
        //handler就是我们配置的typeHandler
        String handlerTypeName = child.getStringAttribute("handler");
        //resolveClass方法就是我们上篇文章所讲的TypeAliasRegistry里面处理别名的方法
        Class<?> javaTypeClass = resolveClass(javaTypeName);
        //JdbcType是一个枚举类型，resolveJdbcType方法是在获取枚举类型的值
        JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
        Class<?> typeHandlerClass = resolveClass(handlerTypeName);
        //注册typeHandler, typeHandler通过TypeHandlerRegistry这个类管理
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

  /**
   <mappers>
       <mapper resource="org/apache/ibatis/builder/BlogMapper.xml"/>
       <mapper url="file:./src/test/java/org/apache/ibatis/builder/NestedBlogMapper.xml"/>
       <mapper class="org.apache.ibatis.builder.CachedAuthorMapper"/>
       <package name="org.apache.ibatis.builder.mapper"/>
   </mappers>

   * 解析配置文件的mappers子节点，方法主要是实现一个大体框架，按照resource->url->class的优先级读取配置，
   * 具体的解析细节是依赖于 XMLMapperBuilder 来实现的，
   * XMLMapperBuilder通过 mapperParser.parse() 方法屏蔽了细节，内部完成解析过程
   * 也正是由于 <mappers> 标签的解析 创建了 XMLMapperBuilder 来解析的  再其构造函数中 又 new 了一个 XPathParser
   * 所以 解析<mappers>标签的XPathParser解析器 与 其他标签的解析器不同！  其他标签的解析器 都是同一个 ！
   */
  private void mapperElement(XNode parent) throws Exception {
    if (parent == null) return; // -modify
    log.warn("开始解析 <mappers> 标签  XNode 地址：" + parent.hashCode());
    //  遍历<mappers>下所有子节点 <mapper> 或 <package>
    for (XNode child : parent.getChildren()) {
      // 如果当前节点为<package>
      if ("package".equals(child.getName())) {
        /**
         * 第一部分：根据注解生成对应的mappedStatement
         * 1.1 处理package类型的配置
         * 获取<package>的name属性（该属性值为mapper class所在的包名）
         * child ---  <package name="org.apache.goat.chapter100.A044"/>
         * mapperPackage ---  org.apache.goat.chapter100.A044
         * 1.2 按照包来添加，扫包之后默认会在包下找与java接口名称相同的mapper映射文件，mapperPackage 就是包名
         * 将该包下的所有Mapper Class注册到configuration的mapperRegistry容器中
         */
        String mapperPackage = child.getStringAttribute("name");
        configuration.addMappers(mapperPackage);
      } else {
        /** 处理 <mapper> 标签 依次获取resource、url、class属性
         * mapper节点配置有3个属性且处理的优先级依次是 ：resource,url,class  三者是互斥的 只能是其中一种
         */
        String resource = child.getStringAttribute("resource");
        String url = child.getStringAttribute("url");
        String mapperClass = child.getStringAttribute("class");
        //1.4 按照resource属性实例化XMLMapperBuilder来解析xml配置文件
        if (resource != null && url == null && mapperClass == null) { // 解析resource属性（Mapper.xml文件的路径）  <mapper resource="org/apache/ibatis/builder/BlogMapper.xml"/>
          log.warn("发现 <mapper> 节点 使用 resource 属性方式");
          ErrorContext.instance().resource(resource);
          InputStream inputStream = Resources.getResourceAsStream(resource); // 将Mapper.xml文件解析成输入流
          // 使用XMLMapperBuilder解析Mapper.xml，并将Mapper Class注册进configuration对象的mapperRegistry容器中
          XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
          //1.5解析配置，因为XMLMapperBuilder继承了BaseBuilder，BaseBuilder内部持有Configuration对象，因此XMLMapperBuilder解析之后直接把配置设置到Configuration对象
          // 第二部分：是解析xml配置，根据xml配置来生成mappedstatement
          mapperParser.parse();
        } else if (resource == null && url != null && mapperClass == null) {  // 解析url属性（Mapper.xml文件的路径） <mapper url="file:./src/test/java/org/apache/ibatis/builder/NestedBlogMapper.xml"/>
          log.warn("发现 <mapper> 节点 使用 url 属性方式");
          //1.6 按照url属性实例化XMLMapperBuilder来解析xml配置文件
          ErrorContext.instance().resource(url);
          InputStream inputStream = Resources.getUrlAsStream(url);
          // 通过读取resource或url属性得到xml的访问路径后，交给XMLMapperBuilder对象来解析
          XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
          mapperParser.parse();
        } else if (resource == null && url == null && mapperClass != null) { // 解析class属性（Mapper Class的全限定名）  <mapper class="org.apache.ibatis.builder.CachedAuthorMapper"/>
          log.warn("发现 <mapper> 节点 使用 class 属性方式");
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

  /**
   * 判断当前传入的子标签id环境 是否是 主标签中指定的环境id
   *  <environments default="pro_mysql">
   *     <environment id="dev_hsqldb"> false
   *     <environment id="pro_mysql">  true
   *     <environment id="test_mysql"> false
   * @param id  每次子标签<environment> 的id值 pro_mysql
   * @return true 当前子标签id值 是 主标签指定的环境
   */
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
