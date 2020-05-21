
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 XMLMapperBuilder用来解析MyBatis中的局部xml配置文件（如上文提到的ProductMapper.xml）
 */
public class XMLMapperBuilder extends BaseBuilder {

  private static final Log log = LogFactory.getLog(XMLConfigBuilder.class);

  // 用来解析XML
  private final XPathParser parser;
  // 再解析完成后，用解析所得的属性来帮助创建各个对象
  private final MapperBuilderAssistant builderAssistant;
  // 保存SQL节点
  private final Map<String, XNode> sqlFragments;
  // <mapper resource="org/apache/goat/chapter100/A/A040/Foo.xml" /> 标签中的resource属性
  private final String resource;

  @Deprecated
  public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
    this(reader, configuration, resource, sqlFragments);
    this.builderAssistant.setCurrentNamespace(namespace);
  }

  @Deprecated
  public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
    this(new XPathParser(reader, true, configuration.getVariables(), new XMLMapperEntityResolver()),configuration, resource, sqlFragments);
  }

  public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
    this(inputStream, configuration, resource, sqlFragments);
    this.builderAssistant.setCurrentNamespace(namespace);
  }

  public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
    this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration, resource, sqlFragments);
  }

  // 最终构造函数
  private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
    // 将configuration赋给父类 BaseBuilder
    super(configuration);
    // MapperBuilderAssistant builderAssistant 全局入口之xml方式
    this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
    this.parser = parser;
    this.sqlFragments = sqlFragments;
    this.resource = resource;
    log.warn("构造函数 202001071500：configuration 地址：" + configuration);
  }

  // 开始解析局部xml  若当前的Mapper.xml尚未被解析，则开始解析
  public void parse() {
    log.warn(  "parse() ：configuration 地址：" + configuration);
    // 防止局部xml被重复解析
    if (!configuration.isResourceLoaded(resource)) {
      // 从<mapper> 根节点开始解析
      configurationElement(parser.evalNode("/mapper"));
      // 当前局部xml解析完成后 将该Mapper.xml添加至configuration的LoadedResource(已解析资源集合)中 防止被重复解析
      configuration.addLoadedResource(resource);
      // 将当前上级循环中解析的mapper.xml对应的Mapper Class  注册进 configuration 的 mapperRegistry 的 knownMappers 容器中 。 通过命名空间绑定 Mapper 接口
      // 将解析的SQL和接口中的方法绑定
      bindMapperForNamespace(builderAssistant.getCurrentNamespace());
    }
    //将resultMap映射信息转换成ResultMap对象
    parsePendingResultMaps();
    //将cache映射信息转换成Cache对象
    parsePendingCacheRefs();
    //将sql映射转换成MappedStatement
    parsePendingStatements();
  }

  public XNode getSqlFragment(String refid) {
    return sqlFragments.get(refid);
  }

  /**
   * 解析局部xml <!ELEMENT mapper (cache-ref | cache | resultMap* | parameterMap* | sql* | insert* | update* | delete* | select* )+>
   * @param context
   */
  private void configurationElement(XNode context) {
    try {
      log.warn("开始解析局部xml配置文件的 <mapper> 标签  XNode 地址：" + context.hashCode());
      // 获取<mapper>节点上的namespace属性，该属性必须存在，表示当前局部xml文件对应的mapper接口是谁
      String namespace = context.getStringAttribute("namespace");
      if (StringUtils.isEmpty(namespace))  throw new BuilderException("Mapper's namespace cannot be empty"); // -modify
      // 记录当前 局部xml的命名空间 org.apache.ibatis.domain.blog.mappers.BlogMapper
      builderAssistant.setCurrentNamespace(namespace);
      // 解析<cache-ref>节点
      cacheRefElement(context.evalNode("cache-ref"));
      // 解析<cache>节点，可以设置缓存类型和属性，或是指定自定义的缓存
      cacheElement(context.evalNode("cache"));
      // 解析<parameterMap>节点,这个已经被废弃，不推荐使用
      parameterMapElement(context.evalNodes("/mapper/parameterMap"));
      // 解析<resultMap>节点
      resultMapElements(context.evalNodes("/mapper/resultMap"));
      // 解析<SQL>节点，SQL节点可以使一些SQL片段被复用
      sqlElement(context.evalNodes("/mapper/sql"));
      // 解析<CRUD>标签 （select|insert|update|delete节点） // 解析 statement
      buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
    }
  }

  private void buildStatementFromContext(List<XNode> list) {
    String databaseId = (configuration.getDatabaseId() != null) ? configuration.getDatabaseId() :null; // -modify
    buildStatementFromContext(list, databaseId);
  }

  private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
    log.warn("开始解析 <select|insert|update|delete> 标签  标签个数：" + list.size());
    for (XNode context : list) {
      // 遍历 "select|insert|update|delete" 节点 为每个节点创建XMLStatementBuilder对象，
      final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
      try {
        statementParser.parseStatementNode();
      } catch (IncompleteElementException e) {
        // 对不能完全解析的节点添加到incompleteStatement，在parsePendingStatements方法中再解析
        configuration.addIncompleteStatement(statementParser);
      }
    }
  }

  private void parsePendingResultMaps() {
    Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
    synchronized (incompleteResultMaps) {
      Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().resolve();
          iter.remove();
        } catch (IncompleteElementException e) {
          // ResultMap is still missing a resource...
        }
      }
    }
  }

  private void parsePendingCacheRefs() {
    Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
    synchronized (incompleteCacheRefs) {
      Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().resolveCacheRef();
          iter.remove();
        } catch (IncompleteElementException e) {
          // Cache ref is still missing a resource...
        }
      }
    }
  }

  private void parsePendingStatements() {
    Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
    synchronized (incompleteStatements) {
      Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().parseStatementNode();
          iter.remove();
        } catch (IncompleteElementException e) {
          // Statement is still missing a resource...
        }
      }
    }
  }
  /**
   *  cacheRefElement方法负责解析cache-ref元素，它通过调用CacheRefResolver的相应方法完成cache的引用。
   *  创建好的cache-ref引用关系存入configuration的cacheRefMap缓存中。
   *
   *  cache-ref–从其他命名空间引用缓存配置。
   *         如果你不想定义自己的cache，可以使用cache-ref引用别的cache。
   *         因为每个cache都以namespace为id，
   *         所以cache-ref只需要配置一个namespace属性就可以了。
   *         需要注意的是，如果cache-ref和cache都配置了，以cache为准。
   */
  private void cacheRefElement(XNode context) {
    if (context == null) return; // -modify
    log.warn("开始解析<cache-ref> 标签");
    configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
    CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, context.getStringAttribute("namespace"));
    try {
      cacheRefResolver.resolveCacheRef();
    } catch (IncompleteElementException e) {
      configuration.addIncompleteCacheRef(cacheRefResolver);
    }
  }

  /** 解析缓存标签   <cache>
   * cache- 配置本定命名空间的缓存。
   *         type- cache实现类，默认为PERPETUAL，可以使用自定义的cache实现类（别名或完整类名皆可）
   *         eviction- 回收算法，默认为LRU，可选的算法有：
   *             LRU– 最近最少使用的：移除最长时间不被使用的对象。
   *             FIFO– 先进先出：按对象进入缓存的顺序来移除它们。
   *             SOFT– 软引用：移除基于垃圾回收器状态和软引用规则的对象。
   *             WEAK– 弱引用：更积极地移除基于垃圾收集器状态和弱引用规则的对象。
   *         flushInterval- 刷新间隔，默认为1个小时，单位毫秒
   *         size- 缓存大小，默认大小1024，单位为引用数
   *         readOnly- 只读
   *
   *    它通过调用CacheBuilder的相应方法完成cache的创建。
   *    每个cache内部都有一个唯一的ID，这个id的值就是namespace。
   *    创建好的cache对象存入configuration的cache缓存中
   *    （该缓存以cache的ID属性即namespace为key，这里再次体现了mybatis的namespace的强大用处）。
   *    <cache eviction="FIFO" size="512" flushInterval="60000"  readOnly="true"/>
   *    <cache type="org.apache.ibatis.submitted.global_variables.CustomCache">
   */
  private void cacheElement(XNode context) {
    log.warn("开始解析<cache> 标签");
    if (context == null) return; // modify
    // 获取type属性
    String type = context.getStringAttribute("type", "PERPETUAL");
    Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
    // 获取eviction属性 默认使用排除算法缓存类型
    String eviction = context.getStringAttribute("eviction", "LRU");
    Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
    // 获取flushInterval属性 缓存自动刷新时间
    Long flushInterval = context.getLongAttribute("flushInterval");
    // 获取size属性 缓存存储实例引用的大小
    Integer size = context.getIntAttribute("size");
    // 是否是只读缓存
    boolean readWrite = !context.getBooleanAttribute("readOnly", false);
    boolean blocking = context.getBooleanAttribute("blocking", false);
    // 获取子节点配置
    Properties props = context.getChildrenAsProperties();
    // 初始化缓存实现
    builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
  }

  private void parameterMapElement(List<XNode> list) {
    for (XNode parameterMapNode : list) {
      String id = parameterMapNode.getStringAttribute("id");
      String type = parameterMapNode.getStringAttribute("type");
      Class<?> parameterClass = resolveClass(type);
      List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
      List<ParameterMapping> parameterMappings = new ArrayList<>();
      for (XNode parameterNode : parameterNodes) {
        String property = parameterNode.getStringAttribute("property");
        String javaType = parameterNode.getStringAttribute("javaType");
        String jdbcType = parameterNode.getStringAttribute("jdbcType");
        String resultMap = parameterNode.getStringAttribute("resultMap");
        String mode = parameterNode.getStringAttribute("mode");
        String typeHandler = parameterNode.getStringAttribute("typeHandler");
        Integer numericScale = parameterNode.getIntAttribute("numericScale");
        ParameterMode modeEnum = resolveParameterMode(mode);
        Class<?> javaTypeClass = resolveClass(javaType);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property, javaTypeClass, jdbcTypeEnum, resultMap, modeEnum, typeHandlerClass, numericScale);
        parameterMappings.add(parameterMapping);
      }
      builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
    }
  }

  /**
   * 该函数用于解析映射文件中所有的<resultMap>节点，这些节点会被解析成ResultMap对象，存储在Configuration对象的resultMaps容器中
   */
  private void resultMapElements(List<XNode> list) throws Exception {
    for (XNode resultMapNode : list) {
      try {
        resultMapElement(resultMapNode);
      } catch (IncompleteElementException e) {
        // ignore, it will be retried
      }
    }
  }

  private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
    return resultMapElement(resultMapNode, Collections.emptyList(), null);
  }

  private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType) throws Exception {
    // 保存当前上下文,用于异常信息回溯
    ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
    // 获取<ResultMap>上的type属性（即resultMap的返回值类型）
    String stringAttribute = resultMapNode.getStringAttribute("ofType", resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType")));
    String type = resultMapNode.getStringAttribute("type",stringAttribute);
    // 将resultMap的返回值类型转换成Class对象
    Class<?> typeClass = resolveClass(type);
    if (typeClass == null) {
      typeClass = inheritEnclosingType(resultMapNode, enclosingType);
    }
    Discriminator discriminator = null;
    // resultMappings用于存储<resultMap>下所有的子节点
    List<ResultMapping> resultMappings = new ArrayList<>();
    resultMappings.addAll(additionalResultMappings);
    List<XNode> resultChildren = resultMapNode.getChildren();
    // 获取并遍历<resultMap>下所有的子节点
    for (XNode resultChild : resultChildren) {
      // 若当前节点为<constructor>，则将它的子节点们添加到resultMappings中去
      if ("constructor".equals(resultChild.getName())) {
        processConstructorElement(resultChild, typeClass, resultMappings);
      } else if ("discriminator".equals(resultChild.getName())) {
        discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
      } else {
        List<ResultFlag> flags = new ArrayList<>();
        if ("id".equals(resultChild.getName())) {
          flags.add(ResultFlag.ID);
        }
        resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
      }
    }
    // 获取<ResultMap>上的id属性
    String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
    // 获取extends 属性
    String extend = resultMapNode.getStringAttribute("extends");
    // 获取autoMapping 属性
    Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
    // ResultMapResolver的作用是生成ResultMap对象，并将其加入到Configuration对象的resultMaps容器中（具体过程见下）
    ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
    try {
      return resultMapResolver.resolve();
    } catch (IncompleteElementException  e) {
      configuration.addIncompleteResultMap(resultMapResolver);
      throw e;
    }
  }

  protected Class<?> inheritEnclosingType(XNode resultMapNode, Class<?> enclosingType) {
    if ("association".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
      String property = resultMapNode.getStringAttribute("property");
      if (property != null && enclosingType != null) {
        MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
        return metaResultType.getSetterType(property);
      }
    } else if ("case".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
      return enclosingType;
    }
    return null;
  }

  private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings) throws Exception {
    List<XNode> argChildren = resultChild.getChildren();
    for (XNode argChild : argChildren) {
      List<ResultFlag> flags = new ArrayList<>();
      flags.add(ResultFlag.CONSTRUCTOR);
      if ("idArg".equals(argChild.getName())) {
        flags.add(ResultFlag.ID);
      }
      resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
    }
  }

  private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType, List<ResultMapping> resultMappings) throws Exception {
    String column = context.getStringAttribute("column");
    String javaType = context.getStringAttribute("javaType");
    String jdbcType = context.getStringAttribute("jdbcType");
    String typeHandler = context.getStringAttribute("typeHandler");
    Class<?> javaTypeClass = resolveClass(javaType);
    Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
    JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
    Map<String, String> discriminatorMap = new HashMap<>();
    for (XNode caseChild : context.getChildren()) {
      String value = caseChild.getStringAttribute("value");
      String resultMap = caseChild.getStringAttribute("resultMap", processNestedResultMappings(caseChild, resultMappings, resultType));
      discriminatorMap.put(value, resultMap);
    }
    return builderAssistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
  }

  private void sqlElement(List<XNode> list) {
    if (configuration.getDatabaseId() != null) {
      sqlElement(list, configuration.getDatabaseId());
    }
    sqlElement(list, null);
  }

  private void sqlElement(List<XNode> list, String requiredDatabaseId) {
    for (XNode context : list) {
      String databaseId = context.getStringAttribute("databaseId");
      String id = context.getStringAttribute("id");
      id = builderAssistant.applyCurrentNamespace(id, false);
      if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
        sqlFragments.put(id, context);
      }
    }
  }

  private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
    if (requiredDatabaseId != null) {
      return requiredDatabaseId.equals(databaseId);
    }
    if (databaseId != null) {
      return false;
    }
    if (!this.sqlFragments.containsKey(id)) {
      return true;
    }
    // skip this fragment if there is a previous one with a not null databaseId
    XNode context = this.sqlFragments.get(id);
    return context.getStringAttribute("databaseId") == null;
  }

  private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) throws Exception {
    String property;
    if (flags.contains(ResultFlag.CONSTRUCTOR)) {
      property = context.getStringAttribute("name");
    } else {
      property = context.getStringAttribute("property");
    }
    String column = context.getStringAttribute("column");
    String javaType = context.getStringAttribute("javaType");
    String jdbcType = context.getStringAttribute("jdbcType");
    String nestedSelect = context.getStringAttribute("select");
    String nestedResultMap = context.getStringAttribute("resultMap", processNestedResultMappings(context, Collections.emptyList(), resultType));
    String notNullColumn = context.getStringAttribute("notNullColumn");
    String columnPrefix = context.getStringAttribute("columnPrefix");
    String typeHandler = context.getStringAttribute("typeHandler");
    String resultSet = context.getStringAttribute("resultSet");
    String foreignColumn = context.getStringAttribute("foreignColumn");
    boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
    Class<?> javaTypeClass = resolveClass(javaType);
    Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
    JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
    return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
  }

  private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType) throws Exception {
    if ("association".equals(context.getName()) || "collection".equals(context.getName()) || "case".equals(context.getName())) {
      if (context.getStringAttribute("select") == null) {
        validateCollection(context, enclosingType);
        ResultMap resultMap = resultMapElement(context, resultMappings, enclosingType);
        return resultMap.getId();
      }
    }
    return null;
  }

  protected void validateCollection(XNode context, Class<?> enclosingType) {
    if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null && context.getStringAttribute("javaType") == null) {
      MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
      String property = context.getStringAttribute("property");
      if (!metaResultType.hasSetter(property)) {
        throw new BuilderException( "Ambiguous collection type for property '" + property + "'. You must specify 'javaType' or 'resultMap'.");
      }
    }
  }

  /**
   *  通过局部xml中的namespace属性 将对应的mapper接口与该局部xml进行绑定
   * @param namespace -  对应 当前局部xml配置文件中命名空间 <mapper namespace="org.apache.ibatis.domain.blog.mappers.BlogMapper"> 中的 namespace 属性
   */
  public void bindMapperForNamespace(String namespace) { // -modify
    if (namespace == null) return; //-modify
    Class<?> boundType = null;
    try {
      // 通过全限定名反射获取对应的Mapper接口对象
      boundType = Resources.classForName(namespace);
    } catch (ClassNotFoundException e) { } // ignore, bound type is not required
    if (boundType == null || configuration.hasMapper(boundType)) return; // -modify
    /**
     * Spring may not know the real resource name so we set a flag to prevent loading again this resource from the mapper interface
     * @see MapperAnnotationBuilder#loadXmlResource
     */
    configuration.addLoadedResource("namespace:" + namespace);
    configuration.addMapper(boundType);
  }

}
