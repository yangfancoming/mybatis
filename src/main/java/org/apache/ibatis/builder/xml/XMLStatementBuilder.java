
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Locale;

/**
 *  XMLStatementBuilder 用来解析 局部配置文件中的SQL语句
 *  用于缓存、sql参数、查询返回的结果集处理
*/
public class XMLStatementBuilder extends BaseBuilder {

  private static final Log log = LogFactory.getLog(XMLStatementBuilder.class);

  private final MapperBuilderAssistant builderAssistant;
  private final XNode context;
  private final String databaseId;

  public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, XNode context) {
    this(configuration, builderAssistant, context, null);
  }

  public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, XNode context, String databaseId) {
    super(configuration);
    this.builderAssistant = builderAssistant;
    this.context = context;
    this.databaseId = databaseId;
    log.warn("构造函数 202001071544：configuration 地址：" + configuration);
  }

  /**
   *解析sql节点的核心方法
   * context 的值为：
   * 	<select id="selectWithOptions" resultType="org.apache.ibatis.domain.blog.Author" fetchSize="200" timeout="10"
   * 	statementType="PREPARED" resultSetType="SCROLL_SENSITIVE" flushCache="false" useCache="false">
   * 		select * from author
   * 	</select>
   */
  public void parseStatementNode() {
    log.warn("0.解析当前节点 <select|insert|update|delete> XNode 地址：" + context.hashCode());
    // <C|R|U|D>标签中的id属性
    String id = context.getStringAttribute("id");
    // <C|R|U|D>标签中的databaseId属性  数据库厂商标识
    String sqlDatabaseId = context.getStringAttribute("databaseId");
    // 验证当前sql的databaseId是否与<environment>标签中配置的数据库是否匹配，不符合则直接返回
    if (!databaseIdMatchesCurrent(id, sqlDatabaseId, databaseId))  return;
    // 获取标签名称 select
    String nodeName = context.getNode().getNodeName();
    // 通过标签名称 解析出对应的枚举类型  SELECT
    SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
    // 判断当前标签CRUD类型 是否为SELECT类型
    boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
    // flushCache：在执行语句时表示是否刷新缓存
    boolean flushCache = context.getBooleanAttribute("flushCache", !isSelect);
    // 是否对该语句进行二级缓存；默认值：对 select 元素为 true
    boolean useCache = context.getBooleanAttribute("useCache", isSelect);
    // 根嵌套结果相关
    boolean resultOrdered = context.getBooleanAttribute("resultOrdered", false);
    // Include Fragments before parsing 引入SQL片段  解析<include>标签
    XMLIncludeTransformer includeParser = new XMLIncludeTransformer(configuration, builderAssistant);
    includeParser.applyIncludes(context.getNode());
    // 参数类型；将会传入这条语句的参数类的完全限定名或别名。这个属性是可选的，因为 MyBatis 可以通过 TypeHandler 推断出具体传入语句的参数，默认值为 unset
    String parameterType = context.getStringAttribute("parameterType");
    Class<?> parameterTypeClass = resolveClass(parameterType);
    // 动态 SQL 中可插拔的脚本语言
    String lang = context.getStringAttribute("lang");
    log.warn("解析 <select|insert|update|delete> 标签的 lang 属性：" + lang);
    // 默认实现类为： XMLLanguageDriver
    LanguageDriver langDriver = getLanguageDriver(lang);
    // Parse selectKey after includes and remove them. 处理selectKey
    processSelectKeyNodes(id, parameterTypeClass, langDriver);
    // Parse the SQL (pre: <selectKey> and <include> were parsed and removed)  为什么要移除呢？秘密都隐藏在 applyIncludes()方法内部了。
    // 设置主键自增的方式
    KeyGenerator keyGenerator;
    String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
    keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
    if (configuration.hasKeyGenerator(keyStatementId)) {
      keyGenerator = configuration.getKeyGenerator(keyStatementId);
    } else {
      keyGenerator = context.getBooleanAttribute("useGeneratedKeys",configuration.isUseGeneratedKeys()  && SqlCommandType.INSERT.equals(sqlCommandType))  ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
    }
    // 解析动态sql 默认实现类为： RawSqlSource
    SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);
    //STATEMENT，PREPARED 或 CALLABLE 的一个。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED
    StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType", StatementType.PREPARED.toString()));
    Integer fetchSize = context.getIntAttribute("fetchSize");
    Integer timeout = context.getIntAttribute("timeout");
    // 已废弃
    String parameterMap = context.getStringAttribute("parameterMap");
    // 结果类型；表示从这条语句中返回的期望类型的类的完全限定名或别名。注意如果是集合情形，那应该是集合可以包含的类型，而不能是集合本身。不能和resultMap同时使用
    String resultType = context.getStringAttribute("resultType");
    Class<?> resultTypeClass = resolveClass(resultType);
    // 结果类型；外部 resultMap 的命名引用
    String resultMap = context.getStringAttribute("resultMap");
    // 结果集类型；FORWARD_ONLY，SCROLL_SENSITIVE 或 SCROLL_INSENSITIVE 中的一个，默认值为 unset （依赖驱动）
    String resultSetType = context.getStringAttribute("resultSetType");
    ResultSetType resultSetTypeEnum = resolveResultSetType(resultSetType);
    String keyProperty = context.getStringAttribute("keyProperty");
    String keyColumn = context.getStringAttribute("keyColumn");
    String resultSets = context.getStringAttribute("resultSets");
    // 通过buildAssistant将解析得到的参数设置构造成 MappedStatement 对象
    builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum, flushCache, useCache, resultOrdered, keyGenerator, keyProperty, keyColumn, sqlDatabaseId, langDriver, resultSets);
  }

  private void processSelectKeyNodes(String id, Class<?> parameterTypeClass, LanguageDriver langDriver) {
    List<XNode> selectKeyNodes = context.evalNodes("selectKey");
    if (configuration.getDatabaseId() != null) {
      parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver, configuration.getDatabaseId());
    }
    parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver, null);
    removeSelectKeyNodes(selectKeyNodes);
  }

  private void parseSelectKeyNodes(String parentId, List<XNode> list, Class<?> parameterTypeClass, LanguageDriver langDriver, String skRequiredDatabaseId) {
    for (XNode nodeToHandle : list) {
      String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
      String databaseId = nodeToHandle.getStringAttribute("databaseId");
      // 验证当前sql的databaseId是否与<environment>标签中配置的数据库是否匹配
      if (databaseIdMatchesCurrent(id, databaseId, skRequiredDatabaseId)) {
        parseSelectKeyNode(id, nodeToHandle, parameterTypeClass, langDriver, databaseId);
      }
    }
  }

  private void parseSelectKeyNode(String id, XNode nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver, String databaseId) {
    String resultType = nodeToHandle.getStringAttribute("resultType");
    Class<?> resultTypeClass = resolveClass(resultType);
    StatementType statementType = StatementType.valueOf(nodeToHandle.getStringAttribute("statementType", StatementType.PREPARED.toString()));
    String keyProperty = nodeToHandle.getStringAttribute("keyProperty");
    String keyColumn = nodeToHandle.getStringAttribute("keyColumn");
    boolean executeBefore = "BEFORE".equals(nodeToHandle.getStringAttribute("order", "AFTER"));
    // defaults   -modify
    KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
    Integer fetchSize = null;
    Integer timeout = null;
    SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
    SqlCommandType sqlCommandType = SqlCommandType.SELECT;
    builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,fetchSize, timeout, null, parameterTypeClass, null, resultTypeClass,null, false, false, false,keyGenerator, keyProperty, keyColumn, databaseId, langDriver, null);
    id = builderAssistant.applyCurrentNamespace(id, false);
    MappedStatement keyStatement = configuration.getMappedStatement(id, false);
    configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
  }

  private void removeSelectKeyNodes(List<XNode> selectKeyNodes) {
    for (XNode nodeToHandle : selectKeyNodes) {
      nodeToHandle.getParent().getNode().removeChild(nodeToHandle.getNode());
    }
  }

  /**
   * 判断当前sql中的databaseId属性 是否匹配 当前连接的数据库的databaseId 属性
   * @param id
   * @param sqlDatabaseId - <CRUD> 标签中的 databaseId 属性
   * @param databaseId -  <environment> 标签中 从数据源中获取的 databaseId 属性
   * @return true 匹配  false 不匹配
   * -modify
   */
  public boolean databaseIdMatchesCurrent(String id, String sqlDatabaseId, String databaseId) {
    if (databaseId != null)  return databaseId.equals(sqlDatabaseId);
    if (sqlDatabaseId != null) return false;
    id = builderAssistant.applyCurrentNamespace(id, false);
    if (!configuration.hasStatement(id, false)) {
      return true;
    }
    // skip this statement if there is a previous one with a not null databaseId
    MappedStatement previous = configuration.getMappedStatement(id, false); // issue #2
    return previous.getDatabaseId() == null;
  }

  private LanguageDriver getLanguageDriver(String lang) {
    Class<? extends LanguageDriver> langClass = null;
    if (lang != null) langClass = resolveClass(lang);
    return configuration.getLanguageDriver(langClass);
  }
}
