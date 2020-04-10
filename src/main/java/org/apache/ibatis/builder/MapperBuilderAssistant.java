
package org.apache.ibatis.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.CacheBuilder;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;


/**
 *  MapperBuilderAssistant 为MapperBuilder的协助者
*/
public class MapperBuilderAssistant extends BaseBuilder {

  // 对应 当前局部xml配置文件中命名空间  eg: <mapper namespace="org.apache.ibatis.domain.blog.mappers.BlogMapper"> 中的 namespace 属性
  private String currentNamespace;
  private final String resource;
  private Cache currentCache;
  private boolean unresolvedCacheRef; // issue #676

  public MapperBuilderAssistant(Configuration configuration, String resource) {
    super(configuration);
    ErrorContext.instance().resource(resource);
    this.resource = resource;
  }

  public String getCurrentNamespace() {
    return currentNamespace;
  }

  public void setCurrentNamespace(String currentNamespace) {
    // 因为该方法不止一个地方调用，所以这里必须增加null的判断
    if (currentNamespace == null) throw new BuilderException("The mapper element requires a namespace attribute to be specified.");
    // 如果 局部xml命名空间已经注册并且已经注册的命名空间值与当前命名空间值不相同的话  则抛出异常
    if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) throw new BuilderException("Wrong namespace. Expected '" + this.currentNamespace + "' but found '" + currentNamespace + "'.");
    this.currentNamespace = currentNamespace;
  }

  /**
   * 带上currentNamespace前缀
   * eg： 输入：getEmpByIdAndLastName2    输出：org.apache.goat.chapter100.C.C012.EmployeeMapper.getEmpByIdAndLastName2
   *  doit 这里P2 isReference 到底是什么意思呢？
  */
  public String applyCurrentNamespace(String base, boolean isReference) {
    if (base == null)  return null; // modify-
    if (isReference) {
      // is it qualified with any namespace yet?
      if (base.contains("."))  return base;
    } else {
      // is it qualified with this namespace yet?
      if (base.startsWith(currentNamespace + ".")) return base;
      // 如果crud的标签id中有.  eg:  <select id="selectById." parameterType="int" resultType="Foo">
      if (base.contains(".")) throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
    }
    // 其他代码都是校检，只有这一语业务代码。。。
    return currentNamespace + "." + base;
  }

  public Cache useCacheRef(String namespace) {
    if (namespace == null) throw new BuilderException("cache-ref element requires a namespace attribute.");
    try {
      unresolvedCacheRef = true;
      Cache cache = configuration.getCache(namespace);
      if (cache == null) throw new IncompleteElementException("No cache for namespace '" + namespace + "' could be found.");
      currentCache = cache;
      unresolvedCacheRef = false;
      return cache;
    } catch (IllegalArgumentException e) {
      throw new IncompleteElementException("No cache for namespace '" + namespace + "' could be found.", e);
    }
  }

  // 使用建造模式构建缓存实例
  public Cache useNewCache(Class<? extends Cache> typeClass, Class<? extends Cache> evictionClass,Long flushInterval,Integer size,boolean readWrite,boolean blocking, Properties props) {
    // 这里构建 Cache 实例采用 Builder 模式，每一个 Namespace 生成一个  Cache 实例
    Cache cache = new CacheBuilder(currentNamespace)
      // Builder前 设置从xml中解析过来的参数
        .implementation(valueOrDefault(typeClass, PerpetualCache.class))
        .addDecorator(valueOrDefault(evictionClass, LruCache.class))
        .clearInterval(flushInterval)
        .size(size)
        .readWrite(readWrite)
        .blocking(blocking)
        .properties(props)
        .build();
    // 添加缓存到 Configuration 对象中
    configuration.addCache(cache);
    // 设置 currentCache 遍历，即当前使用的缓存
    currentCache = cache;
    return cache;
  }

  public ParameterMap addParameterMap(String id, Class<?> parameterClass, List<ParameterMapping> parameterMappings) {
    id = applyCurrentNamespace(id, false);
    ParameterMap parameterMap = new ParameterMap.Builder(configuration, id, parameterClass, parameterMappings).build();
    configuration.addParameterMap(parameterMap);
    return parameterMap;
  }

  public ParameterMapping buildParameterMapping( Class<?> parameterType, String property, Class<?> javaType, JdbcType jdbcType,String resultMap,ParameterMode parameterMode, Class<? extends TypeHandler<?>> typeHandler,Integer numericScale) {
    resultMap = applyCurrentNamespace(resultMap, true);
    // Class parameterType = parameterMapBuilder.type();
    Class<?> javaTypeClass = resolveParameterJavaType(parameterType, property, javaType, jdbcType);
    TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
    return new ParameterMapping.Builder(configuration, property, javaTypeClass).jdbcType(jdbcType).resultMapId(resultMap).mode(parameterMode).numericScale(numericScale).typeHandler(typeHandlerInstance).build();
  }

  public ResultMap addResultMap(String id,Class<?> type, String extend, Discriminator discriminator,List<ResultMapping> resultMappings, Boolean autoMapping) {
    id = applyCurrentNamespace(id, false);
    extend = applyCurrentNamespace(extend, true);
    if (extend != null) {
      if (!configuration.hasResultMap(extend)) throw new IncompleteElementException("Could not find a parent resultmap with id '" + extend + "'");
      ResultMap resultMap = configuration.getResultMap(extend);
      List<ResultMapping> extendedResultMappings = new ArrayList<>(resultMap.getResultMappings());
      extendedResultMappings.removeAll(resultMappings);
      // Remove parent constructor if this resultMap declares a constructor.
      boolean declaresConstructor = false;
      for (ResultMapping resultMapping : resultMappings) {
        if (resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR)) {
          declaresConstructor = true;
          break;
        }
      }
      if (declaresConstructor) {
        extendedResultMappings.removeIf(resultMapping -> resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR));
      }
      resultMappings.addAll(extendedResultMappings);
    }
    ResultMap resultMap = new ResultMap.Builder(configuration, id, type, resultMappings, autoMapping).discriminator(discriminator).build();
    configuration.addResultMap(resultMap);
    return resultMap;
  }

  public Discriminator buildDiscriminator(Class<?> resultType, String column, Class<?> javaType, JdbcType jdbcType, Class<? extends TypeHandler<?>> typeHandler, Map<String, String> discriminatorMap) {
    ResultMapping resultMapping = buildResultMapping(resultType,null, column,javaType,jdbcType,null,null,null,null,typeHandler, new ArrayList<>(),null,null,false);
    Map<String, String> namespaceDiscriminatorMap = new HashMap<>();
    for (Map.Entry<String, String> e : discriminatorMap.entrySet()) {
      String resultMap = e.getValue();
      resultMap = applyCurrentNamespace(resultMap, true);
      namespaceDiscriminatorMap.put(e.getKey(), resultMap);
    }
    return new Discriminator.Builder(configuration, resultMapping, namespaceDiscriminatorMap).build();
  }

  public MappedStatement addMappedStatement(String id,SqlSource sqlSource, StatementType statementType,SqlCommandType sqlCommandType,Integer fetchSize,Integer timeout, String parameterMap,Class<?> parameterType, String resultMap,Class<?> resultType, ResultSetType resultSetType, boolean flushCache,boolean useCache,boolean resultOrdered,KeyGenerator keyGenerator,String keyProperty, String keyColumn,String databaseId,LanguageDriver lang, String resultSets) {
    if (unresolvedCacheRef) throw new IncompleteElementException("Cache-ref not yet resolved");
    // 操作前 deleteById  操作后 org.apache.goat.chapter100.B.B062.FooMapper.deleteById
    id = applyCurrentNamespace(id, false);
    boolean isSelect = (sqlCommandType == SqlCommandType.SELECT);
    // 全局唯一入口 创建 MappedStatement.Builder
    MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType)
        .resource(resource).fetchSize(fetchSize).timeout(timeout).statementType(statementType)
        .keyGenerator(keyGenerator).keyProperty(keyProperty).keyColumn(keyColumn)
        .databaseId(databaseId).lang(lang)
        .resultOrdered(resultOrdered).resultSets(resultSets).resultMaps(getStatementResultMaps(resultMap, resultType, id)).resultSetType(resultSetType)
        .flushCacheRequired(valueOrDefault(flushCache, !isSelect))
        .useCache(valueOrDefault(useCache, isSelect)).cache(currentCache);

    ParameterMap statementParameterMap = getStatementParameterMap(parameterMap, parameterType, id);
    if (statementParameterMap != null) {
      statementBuilder.parameterMap(statementParameterMap);
    }
    // 解析出最终 sql 对应的 MappedStatement 后 保存到全局 Configuration 中
    MappedStatement statement = statementBuilder.build();
    configuration.addMappedStatement(statement);
    return statement;
  }

  private <T> T valueOrDefault(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }

  private ParameterMap getStatementParameterMap(String parameterMapName, Class<?> parameterTypeClass,String statementId) {
    parameterMapName = applyCurrentNamespace(parameterMapName, true);
    ParameterMap parameterMap = null;
    if (parameterMapName != null) {
      try {
        parameterMap = configuration.getParameterMap(parameterMapName);
      } catch (IllegalArgumentException e) {
        throw new IncompleteElementException("Could not find parameter map " + parameterMapName, e);
      }
    } else if (parameterTypeClass != null) {
      List<ParameterMapping> parameterMappings = new ArrayList<>();
      parameterMap = new ParameterMap.Builder(configuration,statementId + "-Inline",parameterTypeClass,parameterMappings).build();
    }
    return parameterMap;
  }

  private List<ResultMap> getStatementResultMaps( String resultMap, Class<?> resultType, String statementId) {
    resultMap = applyCurrentNamespace(resultMap, true);
    List<ResultMap> resultMaps = new ArrayList<>();
    if (resultMap != null) {
      String[] resultMapNames = resultMap.split(",");
      for (String resultMapName : resultMapNames) {
        try {
          resultMaps.add(configuration.getResultMap(resultMapName.trim()));
        } catch (IllegalArgumentException e) {
          throw new IncompleteElementException("Could not find result map '" + resultMapName + "' referenced from '" + statementId + "'", e);
        }
      }
    } else if (resultType != null) {
      ResultMap inlineResultMap = new ResultMap.Builder(configuration,statementId + "-Inline", resultType,new ArrayList<>(),null).build();
      resultMaps.add(inlineResultMap);
    }
    return resultMaps;
  }

  public ResultMapping buildResultMapping(Class<?> resultType,String property,String column,Class<?> javaType, JdbcType jdbcType, String nestedSelect, String nestedResultMap, String notNullColumn,String columnPrefix,Class<? extends TypeHandler<?>> typeHandler,List<ResultFlag> flags,String resultSet,String foreignColumn,boolean lazy) {
    Class<?> javaTypeClass = resolveResultJavaType(resultType, property, javaType);
    TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
    List<ResultMapping> composites = parseCompositeColumnName(column);
    return new ResultMapping.Builder(configuration, property, column, javaTypeClass)
        .jdbcType(jdbcType)
        .nestedQueryId(applyCurrentNamespace(nestedSelect, true))
        .nestedResultMapId(applyCurrentNamespace(nestedResultMap, true))
        .resultSet(resultSet).typeHandler(typeHandlerInstance)
        .flags(flags == null ? new ArrayList<>() : flags)
        .composites(composites)
        .notNullColumns(parseMultipleColumnNames(notNullColumn))
        .columnPrefix(columnPrefix).foreignColumn(foreignColumn).lazy(lazy).build();
  }

  private Set<String> parseMultipleColumnNames(String columnName) {
    Set<String> columns = new HashSet<>();
    if (columnName != null) {
      if (columnName.indexOf(',') > -1) {
        StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
        while (parser.hasMoreTokens()) {
          String column = parser.nextToken();
          columns.add(column);
        }
      } else {
        columns.add(columnName);
      }
    }
    return columns;
  }

  private List<ResultMapping> parseCompositeColumnName(String columnName) {
    List<ResultMapping> composites = new ArrayList<>();
    if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
      StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
      while (parser.hasMoreTokens()) {
        String property = parser.nextToken();
        String column = parser.nextToken();
        ResultMapping complexResultMapping = new ResultMapping.Builder(configuration, property, column, configuration.getTypeHandlerRegistry().getUnknownTypeHandler()).build();
        composites.add(complexResultMapping);
      }
    }
    return composites;
  }

  private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
    if (javaType == null && property != null) {
      try {
        MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
        javaType = metaResultType.getSetterType(property);
      } catch (Exception e) {
        //ignore, following null check statement will deal with the situation
      }
    }
    if (javaType == null) javaType = Object.class;
    return javaType;
  }

  private Class<?> resolveParameterJavaType(Class<?> resultType, String property, Class<?> javaType, JdbcType jdbcType) {
    if (javaType == null) {
      if (JdbcType.CURSOR.equals(jdbcType)) {
        javaType = java.sql.ResultSet.class;
      } else if (Map.class.isAssignableFrom(resultType)) {
        javaType = Object.class;
      } else {
        MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
        javaType = metaResultType.getGetterType(property);
      }
    }
    if (javaType == null) javaType = Object.class;
    return javaType;
  }

  /** Backward compatibility signature. */
  public ResultMapping buildResultMapping(Class<?> resultType, String property, String column, Class<?> javaType,JdbcType jdbcType, String nestedSelect, String nestedResultMap, String notNullColumn, String columnPrefix,Class<? extends TypeHandler<?>> typeHandler, List<ResultFlag> flags) {
    return buildResultMapping(resultType, property, column, javaType, jdbcType, nestedSelect,nestedResultMap, notNullColumn, columnPrefix, typeHandler, flags, null, null, configuration.isLazyLoadingEnabled());
  }

  /**
   * @deprecated Use {@link Configuration#getLanguageDriver(Class)}
   */
  @Deprecated
  public LanguageDriver getLanguageDriver(Class<? extends LanguageDriver> langClass) {
    return configuration.getLanguageDriver(langClass);
  }

  /** Backward compatibility signature. */
  public MappedStatement addMappedStatement(String id, SqlSource sqlSource, StatementType statementType,SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap, Class<?> parameterType,String resultMap, Class<?> resultType, ResultSetType resultSetType, boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty, String keyColumn, String databaseId,LanguageDriver lang) {
    return addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterType, resultMap, resultType, resultSetType, flushCache, useCache, resultOrdered, keyGenerator, keyProperty,keyColumn, databaseId, lang, null);
  }

}
