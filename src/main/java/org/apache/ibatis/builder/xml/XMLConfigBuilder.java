
package org.apache.ibatis.builder.xml;

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
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * XMLConfigBuilder 用来解析MyBatis的全局xml文件  eg: "org/apache/ibatis/submitted/association_nested/mybatis-config.xml"
 */
public class XMLConfigBuilder extends BaseBuilder {

  private static final Log log = LogFactory.getLog(XMLConfigBuilder.class);

  // 标识是否已经解析过全局xml配置文件
  private boolean parsed;
  // 用于解析全局xml配置文件的XPathParser对象
  private final XPathParser parser;
  // 用于保存 <environments default="development"> 标签的default，通过构造函数传入。
  private String environment;
  // 反射工厂，用于创建和缓存反射对象，只是用在 settingsAsProperties() 方法中
  private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

  /**
   *  sos 构造函数报错
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

  /* 【Reader 构造函数】 */
  public XMLConfigBuilder(Reader reader) {
    this(reader, null, null);
  }

  public XMLConfigBuilder(Reader reader, String environment) {
    this(reader, environment, null);
  }

  public XMLConfigBuilder(Reader reader, String environment, Properties props) {
    // System.out.println(111); // 报错：  Constructor call must be the first statement in a constructor
    this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  /* 【InputStream 构造函数】 */
  public XMLConfigBuilder(InputStream inputStream) {
    this(inputStream, null, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment) {
    this(inputStream, environment, null);
  }

  /**
   * @param inputStream 配置文件InputStream
   * @param environment 加载哪种环境(开发环境/生产环境)，包括数据源和事务管理器
   * @param props 属性配置文件，那些属性可以用${propName}语法形式多次用在配置文件中
   */
  public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
    this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  /**
   * 最终构造函数
   *  当创建XMLConfigBuilder对象时，就会初始化Configuration对象，
   *  并且在初始化Configuration对象的时候，一些别名会被注册到Configuration的 typeAliasRegistry 容器中
   * @param parser Mybatis配置文件xml对应的XML解析器
   * @param environment 加载哪种环境(开发环境/生产环境)，包括数据源和事务管理器
   * @param props 属性配置文件，那些属性可以用${propName}语法形式多次用在配置文件中
   */
  private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
    // 新建一个Mybatis全局配置信息类对象
    super(new Configuration());
    // 设置错误报文实例的资源引用
    ErrorContext.instance().resource("SQL Mapper Configuration");
    this.parsed = false; // parse表示是否已经解析了
    this.parser = parser;
    this.environment = environment;
    configuration.setVariables(props); // 设置属性文件的变量到配置类configuration
    log.warn(" 构造函数1736：XPathParser 地址：" + parser);
    log.warn(" 构造函数1736：configuration 地址：" + configuration);
  }

  // 外部调用此方法对mybatis的全局xml文件进行解析
  /**
   * 对Mybatis全局配置文件xml中的<configuation>标签信息进行解析封装，然后添加到Mybatis全局配置信息Configuration中
   * @return Mybatis全局配置信息对象Configuration
   */
  public Configuration parse() {
    log.warn("开始解析全局xml配置文件");
    // 1.判断是否已经解析过，不重复解析 // 判断是否已经完成对mybatis-config.xml配置文件的解析  // 每个XMLConfig Builder只能使用一次;
    if (parsed) throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    parsed = true;
    /**  解析 xml 全局配置文件
     注意一个 xpath 表达式 /configuration， 这个表达式 代表的是 MyBatis 全局xml文件的 <configuration> 节点
     这里通过 xpath 选中这个节点，并传递给 parseConfiguration 方法， 即： 在mybatis-config.xml配置文件中查找<configuration>节点，并开始解析
     */
    XNode xNode = parser.evalNode("/configuration");
    log.warn("开始解析 <configuration> 标签  XNode 地址：" + xNode.hashCode());
    // 2.完成全局xml文件下的configuration节点下的所有标签的解析
    parseConfiguration(xNode);
    return configuration;
  }

  /**
   * 解析mybatis-config.xml所有标签信息，并实例化标签对应的对象，配置进 {@link XMLConfigBuilder#configuration} 里
   * 解析核心配置文件的关键方法，
   *
   * 读取节点的信息，并通过对应的方法去解析配置，解析到的配置全部会放在configuration里面
   * <!ELEMENT configuration (properties?, settings?, typeAliases?, typeHandlers?, objectFactory?, objectWrapperFactory?, reflectorFactory?, plugins?, environments?, databaseIdProvider?, mappers?)>
   * @param root   对应<configuation> 标签
   * */
  private void parseConfiguration(XNode root) {
    try {
      // issue #117 read properties first
      XNode propertiesNode = root.evalNode("properties");
      // 最先解析<properties>节点 并保存到Configuration对象中
      propertiesElement(propertiesNode);
      // 解析<settings>节点 将其转换为Properties对象，并判断解析出的settings属性是否合法(在Configuration对象中存在)
      Properties settings = settingsAsProperties(root.evalNode("settings"));
      // 根据<settings>中的配置，动态加载vfs
      loadCustomVfs(settings);
      // 根据<settings>中的配置，动态加载自定义日志
      loadCustomLogImpl(settings);
      // 解析<typeAliases>节点，并保存到Configuration对象中
      typeAliasesElement(root.evalNode("typeAliases"));
      // 解析<plugins>节点，并保存到Configuration对象中
      pluginElement(root.evalNode("plugins"));
      // 解析 objectFactory 配置  mybatis为结果创建对象时都会用到objectFactory
      objectFactoryElement(root.evalNode("objectFactory"));
      // 解析 objectWrapperFactory 配置
      objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
      // 解析<reflectorFactory>节点
      reflectorFactoryElement(root.evalNode("reflectorFactory"));
      // settings 中的信息设置到 Configuration 对象中
      settingsElement(settings);
      // read it after objectFactory and objectWrapperFactory issue #631  // 解析<environments>标签
      environmentsElement(root.evalNode("environments"));
      // 解析<databaseIdProvider>标签，获取并设置 databaseId 到 Configuration 对象
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));
      // 解析 typeHandlers 配置  当MyBatis设置参数到PreparedStatement 或者从ResultSet 结果集中取得值时，就会使用TypeHandler  来处理数据库类型与java 类型之间转换
      typeHandlerElement(root.evalNode("typeHandlers"));
      // 解析<mappers>节点
      mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
  }

  /**
   *  1.解析全局xml配置文件中的<settings>标签，并将解析结果转成 Properties 对象
   *  2.转成 Properties 对象后 进行一次校检
   * @param context <settings>标签
   */
  /**
   * 读取<setting></setting>便签信息，每个<setting/>的name属性对应着Configuration的属性变量名，
   * 该放会检验<setting/>标签的name属性能不能在Configuration查找出来，如果查找不出来，抛出异常
   */
  private Properties settingsAsProperties(XNode context) {
    if (context == null) return new Properties();
    /**
     * 解析<settings>标签，并返回Properties对象，其中name属性为key ，value属性为value
     *   <settings>
     *     <setting name="mapUnderscoreToCamelCase" value="true"/>
     *     <setting name="cacheEnabled" value="true" />
     *   </settings>
     * "mapUnderscoreToCamelCase" -> "true"
     * "cacheEnabled" -> "true"
     */
    Properties props = context.getChildrenAsProperties();
    // Check that all settings are known to the configuration class 检查Configuration配置类是否知道 所有配置。
    MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
    for (Object key : props.keySet()) {
      // 检测 Configuration 中是否存在相关属性，不存在则抛出异常 (说白了就是判断 settings 标签中配置的name属性 必须得是mybatis内置的，不能是自己随便写的。)
      if (!metaConfig.hasSetter(String.valueOf(key))) {
        throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
      }
    }
    return props;
  }

  /**
   * 加载虚拟文件系统配置，读取服务器资源
   * VFS含义是虚拟文件系统；主要是通过程序能够方便读取本地文件系统、FTP文件系统等系统中的文件资源。
   * Mybatis中提供了VFS这个配置，主要是通过该配置可以加载自定义的虚拟文件系统应用程序
   */
  private void loadCustomVfs(Properties settings) throws ClassNotFoundException {
    String value = settings.getProperty("vfsImpl");
    if (value == null) return; // modify
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

  /**
   * 制定日志的实现(log4j等)
   *    <settings>
   *        <setting name="logImpl" value="NO_LOGGING"/>
   *    </settings>
   从 MyBatis 的 TypeAliasRegistry 中查找 logImpl 键所对应值的类对象
   这里 logImpl 对应的 value 值可以从 org.apache.ibatis.session.Configuration 的构造方法中找到
   注意 Log 类，这是 MyBatis 内部对日志对象的抽象
   */
  private void loadCustomLogImpl(Properties settings) {
    Class<? extends Log> logImpl = resolveClass(settings.getProperty("logImpl"));
    // 将查找到的 Class 对象设置到 Configuration 对象中
    configuration.setLogImpl(logImpl);
  }

  /**
   * 如果<typeAliases>节点下定义了<package>节点，那么MyBatis会给该包下的所有类起一个别名（以类名首字母小写作为别名）
   * 如果<typeAliases>节点下定义了<typeAlias>节点，那么MyBatis就会给指定的类起指定的别名。
   * 这些别名都会被存入configuration的typeAliasRegistry容器中。
   * @param parent   对应<typeAliases> 标签
   */
  private void typeAliasesElement(XNode parent) {
    if (parent == null)  return; // --modify
    log.warn("开始解析 <typeAliases> 标签  XNode 地址：" + parent.hashCode());
    // 遍历<typeAliases>下的所有子节点  dtd约束该标签下 只能出现 <package> 或 <typeAlias> 标签
    for (XNode child : parent.getChildren()) {
      // 若为 <package> 则获取其name属性  eg：<package name="org.apache.goat.common"/>
      if ("package".equals(child.getName())) {
        String typeAliasPackage = child.getStringAttribute("name");
        // 为该包下的所有类起个别名，并注册进configuration的typeAliasRegistry中的typeAliases
        typeAliasRegistry.registerAliases(typeAliasPackage); // -modify
      } else {
        // 若为<typeAlias> 则获取其alias和type属性
        String alias = child.getStringAttribute("alias");
        String type = child.getStringAttribute("type");
        try {
          // 通过全限定类名 反射获取模板类
          Class<?> clazz = Resources.classForName(type);
          // 这里直接处理类，别名没有配置也没关系，里面会生成一个getSimpleName或者根据Alias注解去取别名
          // 和<package> 一样会注册进configuration的typeAliasRegistry中的typeAliases
          if (alias == null) {
            typeAliasRegistry.registerAlias(clazz);
          } else {
            typeAliasRegistry.registerAlias(alias, clazz);
          }
        } catch (ClassNotFoundException e) { //4.其他类型直接报错
          throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
        }
      }
    }
  }

  /**
   * <plugins/>元素，每个插件就是mybatis的拦截器。
   * @param parent <plugins/>标签
   *   <plugins>
   *     <plugin interceptor="org.apache.goat.chapter200.D10.MyFirstPlugin">
   *       <property name="username" value="goat"/>
   *       <property name="password" value="123654"/>
   *     </plugin>
   *     <plugin interceptor="org.apache.goat.chapter200.D10.MySecondPlugin"/>
   *   </plugins>
   */
  private void pluginElement(XNode parent) throws Exception {
    if (parent == null) return;  // -modify
    log.warn("开始解析 <plugins> 标签  XNode 地址：" + parent.hashCode());
    // 遍历<plugins>标签  获取<plugin>
    for (XNode child : parent.getChildren()) {
      // 获取 单个 <plugin>标签下的所有 <property>标签
      Properties properties = child.getChildrenAsProperties();
      // 获取 <plugin>标签中的 interceptor 属性 ：org.apache.goat.chapter200.D10.MyMybatisPlugin
      String interceptor = child.getStringAttribute("interceptor");
      // 根据 插件类的全限定名 通过反射生成拦截器的实例
      Class<?> aClass = resolveClass(interceptor);
      Interceptor interceptorInstance = (Interceptor) aClass.newInstance();
      // 再拿<plugin>标签下的所有<property>标签，解析name和value属性成为一个Properties，将Properties设置到拦截器中 （调用我们自定义的重写的 setProperties() 方法）
      interceptorInstance.setProperties(properties);
      // 最终添加到 configuration 的拦截器链中
      configuration.addInterceptor(interceptorInstance);
    }
  }

  /**
   *  ObjectFactory 标签 -- [对象工厂 {@link ObjectFactory}]
   * @param context
   * @throws Exception
   */
  private void objectFactoryElement(XNode context) throws Exception {
    if (context == null) return; // -modify
    log.warn("开始解析 <objectFactory> 标签  XNode 地址：" + context.hashCode());
    String type = context.getStringAttribute("type");
    Properties properties = context.getChildrenAsProperties();
    ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
    factory.setProperties(properties);
    configuration.setObjectFactory(factory);
  }

  /**
   *  ObjectWrapperFactory 标签 -- [对象包装类工厂{@link ObjectWrapperFactory}]
   * @param context
   * @throws Exception
   */
  private void objectWrapperFactoryElement(XNode context) throws Exception {
    if (context == null) return; // -modify
    String type = context.getStringAttribute("type");
    ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).newInstance();
    configuration.setObjectWrapperFactory(factory);
  }

  /**
   * reflectorFactory标签 -- [反射信息类工厂{@link ReflectorFactory}]
   * @param context
   * @throws Exception
   */
  private void reflectorFactoryElement(XNode context) throws Exception {
    if (context == null) return; // -modify
    String type = context.getStringAttribute("type");
    ReflectorFactory factory = (ReflectorFactory) resolveClass(type).newInstance();
    configuration.setReflectorFactory(factory);
  }

  /**
   * 读取<Properties>标签的属性变量，以及合并configuration的属性变量，然后重新赋值到parser和configuration的属性变量中。
   * 1.首先读取在 properties 元素体中指定的属性；
   * 2.其次，读取从 properties 元素的类路径 resource 或 url 指定的属性，且会覆盖已经指定了的重复属性；
   * 3.最后，读取作为方法参数传递的属性，且会覆盖已经从 properties 元素体和 resource 或 url 属性中加载了的重复属性。
   * 因此，三种优先级从低到高分别为： <property> 子节点指定的属性 <  resource 或 url 指定的配置文件中的属性  <  通过方法参数传递的props 属性
   *  @param context 对应 全局 <configuration> 标签下的 <properties> 标签
   */
  private void propertiesElement(XNode context) throws Exception {
    if (context == null) return; // -modify
    log.warn("开始解析 <properties> 标签  XNode 地址：" + context.hashCode());
    /**
     * 1.首先 读取在 <property> 子节点中的所有属性值 eg：11,22,33,44
     * 获取<properties>节点的所有子节点 并将这些节点内容转换为Properties对象
     * 情况一： defaults =  size为0
     *  <properties resource="dbconfig.properties"/>
     *
     * 情况二： defaults =  size 为 4
     *   <properties resource="dbconfig.properties">
     *     <property name="jdbc.driver" value="11"/>
     *     <property name="jdbc.url" value="22"/>
     *     <property name="jdbc.username" value="33"/>
     *     <property name="jdbc.password" value="44"/>
     *   </properties>
     */
    Properties defaults = context.getChildrenAsProperties(); // 获取<Properties>标签下的所有<property> 子标签的属性键值对
    // 获取<properties>节点上的resource属性  eg: resource="dbconfig.properties"
    String resource = context.getStringAttribute("resource");
    // 获取<properties>节点上的url属性
    String url = context.getStringAttribute("url");
    // resource 和 url 两个属性不能同时存在，如果同时存在则抛出异常
    if (resource != null && url != null) {
      throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference. Please specify one or the other.");
    }
    if (resource != null) {
      // 2.其次，读取从 <properties> 父标签中resource或url指定的配置文件(dbconfig.properties)中的内容，且会覆盖步骤1中(<property>子节点)的重复的属性； eg：1,2,3,4
      Properties resourceAsProperties = Resources.getResourceAsProperties(resource);
      // 覆盖掉步骤1中的重复属性(如果有的话)
      defaults.putAll(resourceAsProperties);
    } else if (url != null) {
      // 获取url属性值对应的properties文件中的键值对，并添加至defaults容器中  会产生覆盖操作
      defaults.putAll(Resources.getUrlAsProperties(url));
    }
    /**
     *  3.最后，读取作为方法参数传递的属性，且会覆盖已经从 properties 元素体和 resource 或 url 属性中加载了的重复属性。
     *  入口地址
     * @see XMLConfigBuilder#XMLConfigBuilder(org.apache.ibatis.parsing.XPathParser, java.lang.String, java.util.Properties)
    */
    Properties vars = configuration.getVariables();
    if (vars != null) defaults.putAll(vars);
    parser.setVariables(defaults);
    configuration.setVariables(defaults);
    log.warn(  " propertiesElement()：解析<properties> 标签完毕 ：" +  defaults);
  }

  /**
   * 将<setting/>标签的设置全部设置进去Configuation
   */
  private void settingsElement(Properties settings) {
    // 设置 autoMappingBehavior 属性，默认值为 PARTIAL
    configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(settings.getProperty("autoMappingBehavior", "PARTIAL")));
    configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(settings.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
    // 设置 cacheEnabled 属性，默认值为 true
    configuration.setCacheEnabled(booleanValueOf(settings.getProperty("cacheEnabled"), true));
    configuration.setProxyFactory((ProxyFactory) createInstance(settings.getProperty("proxyFactory")));
    configuration.setLazyLoadingEnabled(booleanValueOf(settings.getProperty("lazyLoadingEnabled"), false));
    configuration.setAggressiveLazyLoading(booleanValueOf(settings.getProperty("aggressiveLazyLoading"), false));
    configuration.setMultipleResultSetsEnabled(booleanValueOf(settings.getProperty("multipleResultSetsEnabled"), true));
    configuration.setUseColumnLabel(booleanValueOf(settings.getProperty("useColumnLabel"), true));
    configuration.setUseGeneratedKeys(booleanValueOf(settings.getProperty("useGeneratedKeys"), false));
    String property = settings.getProperty("defaultExecutorType", "SIMPLE");
    // 若全局配置 <setting name="defaultExecutorType" value="FUCK"/> 这里会抛出异常 No enum constant org.apache.ibatis.session.ExecutorType.FUCK    1
    configuration.setDefaultExecutorType(ExecutorType.valueOf(property));
    configuration.setDefaultStatementTimeout(integerValueOf(settings.getProperty("defaultStatementTimeout"), null));
    configuration.setDefaultFetchSize(integerValueOf(settings.getProperty("defaultFetchSize"), null));
    configuration.setMapUnderscoreToCamelCase(booleanValueOf(settings.getProperty("mapUnderscoreToCamelCase"), false));
    configuration.setSafeRowBoundsEnabled(booleanValueOf(settings.getProperty("safeRowBoundsEnabled"), false));
    configuration.setLocalCacheScope(LocalCacheScope.valueOf(settings.getProperty("localCacheScope", "SESSION")));
    configuration.setJdbcTypeForNull(JdbcType.valueOf(settings.getProperty("jdbcTypeForNull", "OTHER")));
    configuration.setLazyLoadTriggerMethods(stringSetValueOf(settings.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
    configuration.setSafeResultHandlerEnabled(booleanValueOf(settings.getProperty("safeResultHandlerEnabled"), true));
    configuration.setDefaultScriptingLanguage(resolveClass(settings.getProperty("defaultScriptingLanguage")));
    // 解析默认的枚举处理器
    configuration.setDefaultEnumTypeHandler(resolveClass(settings.getProperty("defaultEnumTypeHandler")));
    configuration.setCallSettersOnNulls(booleanValueOf(settings.getProperty("callSettersOnNulls"), false));
    configuration.setUseActualParamName(booleanValueOf(settings.getProperty("useActualParamName"), true));
    configuration.setReturnInstanceForEmptyRow(booleanValueOf(settings.getProperty("returnInstanceForEmptyRow"), false));
    configuration.setLogPrefix(settings.getProperty("logPrefix"));
    configuration.setConfigurationFactory(resolveClass(settings.getProperty("configurationFactory")));
  }

  /**
   * &lt;Environments/&gt;标签元素
   * <p>
   *     获取&lt;Environments/&gt;下的default属性取得当前Mybatis所需要的环境ID并赋值给 {@link XMLConfigBuilder#environment},
   *     然后获取&lt;environment/&gt;的id属性(环境ID)并赋值给{@link @id},判断{@link @id}是不是{@link XMLConfigBuilder#environment},
   *     是就做以下操作
   *     <ol>
   *         <li>获取环境ID</li>
   *         <li>获取事务管理器工厂的实例对象,赋值给 {@link @txFactory}</li>
   *         <li>获取数据库数据源工厂的实例对象{@link @dsFactory}，然后从{@link @dsFactory} 获取数据源，赋值给 {@link @dataSoure}</li>
   *         <li>传入{@link @txFactory},{@link @dataSoure},{@link @id} 构建 {@link Environment} 传入 {@link XMLConfigBuilder#configuration}</li>
   *     </ol>
   * </p>
   * @param context
   * @throws Exception
   */
  private void environmentsElement(XNode context) throws Exception {
    if (context == null) return; // -modify
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
      // ① <environment id="pro_mysql">
      String id = child.getStringAttribute("id");
      // 不是 主标签中指定的环境 则直接忽略
      if (!isSpecifiedEnvironment(id)) continue; // -modify
      // ② 解析 <transactionManager type="JDBC"/>标签，从而获取TransactionFactory接口的实现类
      TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
      // 解析 <dataSource>  标签
      XNode dsNode = child.evalNode("dataSource");
      DataSourceFactory dsFactory = dataSourceElement(dsNode);
      // ③ 获取DataSource接口的实现类
      DataSource dataSource = dsFactory.getDataSource();
      // ④ 再获取了 ①  ②  ③ 后，使用建造者模式 创建Environment对象
      Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory).dataSource(dataSource);
      // 最终完成 configuration 实例中的Environment对象设置
      configuration.setEnvironment(environmentBuilder.build());
      return;// -modify  一旦处理了对应的环境则直接结束
    }
  }

  /**
   * 解析 <databaseIdProvider> 标签
   * 为了后面解析 <C|R|U|D>标签中的databaseId属性 做好两个准备工作。
   * 1.解析<databaseIdProvider>标签下的 所有<property>标签 保存到 VendorDatabaseIdProvider 类的properties中
   * 2.获取 <environments> 标签中最终连接数据源的数据库厂商标识  保存到 Configuration 类的databaseId中
   * @param context
   *   <databaseIdProvider type="DB_VENDOR">
   *     <property name="MySQL" value="mysql"/>
   *     <property name="Oracle" value="oracle"/>
   *     <property name="SQL Server" value="sqlserver"/>
   *     <property name="HSQL Database Engine" value="hsqldb"/>
   *   </databaseIdProvider>
  */
  private void databaseIdProviderElement(XNode context) throws Exception {
    DatabaseIdProvider databaseIdProvider = null;
    if (context != null) {
      String type = context.getStringAttribute("type");
      // awful patch to keep backward compatibility
      if ("VENDOR".equals(type)) type = "DB_VENDOR";
      // 解析<databaseIdProvider>标签下的 所有<property>标签
      Properties properties = context.getChildrenAsProperties();
      // 通过 DB_VENDOR 从别名map中 取出接口实现类 VendorDatabaseIdProvider
      databaseIdProvider = (DatabaseIdProvider) resolveClass(type).newInstance();
      // 保存 全局配置文件中配置的数据库厂商别名
      databaseIdProvider.setProperties(properties);
    }
    Environment environment = configuration.getEnvironment();
    if (environment != null && databaseIdProvider != null) {
      // 从<environments>标签中获取的 databaseId  全局唯一入口
      String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
      configuration.setDatabaseId(databaseId);
    }
  }

  private TransactionFactory transactionManagerElement(XNode context) throws Exception {
    if (context == null) throw new BuilderException("Environment declaration requires a TransactionFactory.");// -modify
    log.warn("开始解析 <transactionManager> 标签  XNode 地址：" + context.hashCode());
    String type = context.getStringAttribute("type");
    // Configuration 的构造函数中初始化了 JdbcTransactionFactory和ManagedTransactionFactory实现类
    TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
    Properties props = context.getChildrenAsProperties();
    factory.setProperties(props);
    return factory;
  }

  /**
   *  解析 <dataSource> 标签并获取 DataSource 四大元素
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
      /**
       *  Configuration 的构造函数中初始化了
       * typeAliasRegistry.registerAlias("JNDI", JndiDataSourceFactory.class);
       * typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
       * typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
      */
      DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
      // 得到 DataSource 四大<property>元素
      Properties props = context.getChildrenAsProperties();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a DataSourceFactory.");
  }

  private void typeHandlerElement(XNode parent) {
    if (parent == null) return; // -modify
    log.warn("开始解析 <typeHandlers> 标签  XNode 地址：" + parent.hashCode());
    for (XNode child : parent.getChildren()) {
      // 子节点为package时，获取其name属性的值，然后自动扫描package下的自定义typeHandler
      if ("package".equals(child.getName())) {
        String typeHandlerPackage = child.getStringAttribute("name");
        typeHandlerRegistry.register(typeHandlerPackage);
      } else {
        // 子节点为typeHandler时， 可以指定javaType属性， 也可以指定jdbcType, 也可两者都指定
        // javaType 是指定java类型
        // jdbcType 是指定jdbc类型（数据库类型： 如varchar）
        String javaTypeName = child.getStringAttribute("javaType");
        String jdbcTypeName = child.getStringAttribute("jdbcType");
        // handler就是我们配置的typeHandler
        String handlerTypeName = child.getStringAttribute("handler");
        // resolveClass方法就是我们上篇文章所讲的TypeAliasRegistry里面处理别名的方法
        Class<?> javaTypeClass = resolveClass(javaTypeName);
        // JdbcType是一个枚举类型，resolveJdbcType方法是在获取枚举类型的值
        JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
        Class<?> typeHandlerClass = resolveClass(handlerTypeName);
        // 注册typeHandler, typeHandler通过TypeHandlerRegistry这个类管理
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
    //  遍历<mappers>下所有子节点 <package> 或 <mapper>
    for (XNode child : parent.getChildren()) {
      if ("package".equals(child.getName())) { // 如果当前节点为<package>
        /**
         * 第一部分：根据注解生成对应的mappedStatement
         * 1.2 按照包来添加，扫包之后默认会在包下找与java接口名称相同的mapper映射文件，mapperPackage 就是包名
         * 将该包下的所有Mapper Class注册到configuration的mapperRegistry容器中
         */
        // 获取<package name="org.apache.goat.chapter100.A.A044"/>中的name属性
        String mapperPackage = child.getStringAttribute("name");
        configuration.addMappers(mapperPackage);
      } else { // 如果当前节点为<mapper>
        // mapper节点配置有3个属性且处理的优先级依次是 ：resource,url,class  三者是互斥的 只能是其中一种
        String resource = child.getStringAttribute("resource");
        String url = child.getStringAttribute("url");
        String mapperClass = child.getStringAttribute("class");
        // 1.4 按照resource属性实例化XMLMapperBuilder来解析xml配置文件
        if (resource != null && url == null && mapperClass == null) { // 解析resource属性（Mapper.xml文件的路径）  <mapper resource="org/apache/ibatis/builder/BlogMapper.xml"/>
          log.warn("发现 <mapper> 节点 使用 resource 属性方式");
          ErrorContext.instance().resource(resource);
          InputStream inputStream = Resources.getResourceAsStream(resource); // 将Mapper.xml文件解析成输入流
          // 使用XMLMapperBuilder解析Mapper.xml，并将Mapper Class注册进configuration对象的mapperRegistry容器中
          XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
          // 1.5解析配置，因为XMLMapperBuilder继承了BaseBuilder，BaseBuilder内部持有Configuration对象，因此XMLMapperBuilder解析之后直接把配置设置到Configuration对象
          // 第二部分：是解析xml配置，根据xml配置来生成mappedstatement
          mapperParser.parse();
        } else if (resource == null && url != null && mapperClass == null) {  // 解析url属性（Mapper.xml文件的路径） <mapper url="file:./src/test/java/org/apache/ibatis/builder/NestedBlogMapper.xml"/>
          log.warn("发现 <mapper> 节点 使用 url 属性方式");
          // 1.6 按照url属性实例化XMLMapperBuilder来解析xml配置文件
          ErrorContext.instance().resource(url);
          InputStream inputStream = Resources.getUrlAsStream(url);
          // 通过读取resource或url属性得到xml的访问路径后，交给XMLMapperBuilder对象来解析
          XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
          mapperParser.parse();
        } else if (resource == null && url == null && mapperClass != null) { // 解析class属性（Mapper Class的全限定名）  <mapper class="org.apache.ibatis.builder.CachedAuthorMapper"/>
          log.warn("发现 <mapper> 节点 使用 class 属性方式");
          // 1.7 按照class属性实例化XMLMapperBuilder来解析xml配置文件
          // 将Mapper Class的全限定名转化成Class对象
          Class<?> mapperInterface = Resources.classForName(mapperClass);
          // 注册进configuration对象的mapperRegistry容器中
          configuration.addMapper(mapperInterface);
        } else {
          // resource、url和class三者是互斥的，配置了多个或者不配置都抛出异常
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
