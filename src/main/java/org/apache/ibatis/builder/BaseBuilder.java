
package org.apache.ibatis.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * BaseBuilder 并没有对子类进行任何’约束’, 只是重复性代码的容器
 * 这个父类维护了一个全局的Configuration对象，MyBatis的配置文件解析后就以Configuration对象的形式存储。
 * MapperBuilderAssistant
 * ParameterMappingTokenHandler
 * SqlSourceBuilder
 * XMLConfigBuilder ： 解析mybatis中configLocation属性中的全局xml文件，内部会使用XMLMapperBuilder解析各个xml文件。
 * XMLMapperBuilder ： 遍历mybatis中mapperLocations属性中的xml文件中每个节点的Builder，比如user.xml，内部会使用XMLStatementBuilder处理xml中的每个节点。
 * XMLStatementBuilder ： 解析xml文件中各个节点，比如select,insert,update,delete节点，内部会使用XMLScriptBuilder处理节点的sql部分，遍历产生的数据会丢到Configuration的mappedStatements中。
 * XMLScriptBuilder ： 解析xml中各个节点sql部分的Builder。
*/
public abstract class BaseBuilder {

  private static final Log log = LogFactory.getLog(BaseBuilder.class);

  // Mybatis初始化过程的核心对象，Mybatis中几乎全部的配置信息会保存到该对象中。该对象在Mybatis初始化过程中创建且是全局唯一的
  protected final Configuration configuration;
  // 定义的别名都会记录在该对象中
  protected final TypeAliasRegistry typeAliasRegistry;
  // 指定数据库类型与Java类型的转换器
  protected final TypeHandlerRegistry typeHandlerRegistry;

  public BaseBuilder(Configuration configuration) {
    log.warn(  " 构造函数1740：this.configuration 地址：" + configuration);
    this.configuration = configuration;
    this.typeAliasRegistry = configuration.getTypeAliasRegistry(); // -modify
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();  // -modify
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  // 获取正则表达式对象,如果regex表达式为null,则使用默认值defaultValue作为表达式
  protected Pattern parseExpression(String regex, String defaultValue) {
    return Pattern.compile(regex == null ? defaultValue : regex);
  }

  // 获取value的boolean值,如果为null,则使用默认值defaultValue
  protected Boolean booleanValueOf(String value, Boolean defaultValue) {
    return value == null ? defaultValue : Boolean.valueOf(value);
  }

  // 获取整形value值,如果为null,则使用默认值defaultValue
  protected Integer integerValueOf(String value, Integer defaultValue) {
    return value == null ? defaultValue : Integer.valueOf(value);
  }

  // 获取value的值,按照‘,’分割为数组并转为hashset,如果value为null,则使用默认值defaultValue
  protected Set<String> stringSetValueOf(String value, String defaultValue) {
    value = value == null ? defaultValue : value;
    return new HashSet<>(Arrays.asList(value.split(",")));
  }

  // 根据别名查询对应的JDBC数据类型,JdbcType是mybatis对java.sql.Types的一次包装,并且是个枚举类,详细的信息可以查看org.apache.ibatis.type.JdbcType
  protected JdbcType resolveJdbcType(String alias) {
    if (alias == null) return null;
    try {
      return JdbcType.valueOf(alias);
    } catch (IllegalArgumentException e) {
      throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
    }
  }

  /**
   根据别名获取对应的结果集,详细信息参照org.apache.ibatis.mapping.ResultSetType。该类是对java.sql.ResultSet的包装,java.sql.ResultSet提供了三个值。
   ResultSet.TYPE_FORWORD_ONLY 结果集的游标只能向下滚动。
   ResultSet.TYPE_SCROLL_INSENSITIVE 结果集的游标可以上下移动，当数据库变化时，当前结果集不变。
   ResultSet.TYPE_SCROLL_SENSITIVE 返回可滚动的结果集，当数据库变化时，当前结果集同步改变。
  */
  protected ResultSetType resolveResultSetType(String alias) {
    if (alias == null) return null;
    try {
      return ResultSetType.valueOf(alias);
    } catch (IllegalArgumentException e) {
      throw new BuilderException("Error resolving ResultSetType. Cause: " + e, e);
    }
  }

  // 根据别名获取ParameterMode类型,可选值为IN, OUT, INOUT,详细信息可参照org.apache.ibatis.mapping.ParameterMode类
  protected ParameterMode resolveParameterMode(String alias) {
    if (alias == null) return null;
    try {
      return ParameterMode.valueOf(alias);
    } catch (IllegalArgumentException e) {
      throw new BuilderException("Error resolving ParameterMode. Cause: " + e, e);
    }
  }

  protected Object createInstance(String alias) {
    Class<?> clazz = resolveClass(alias);
    if (clazz == null) return null;
    try {
      return resolveClass(alias).newInstance();
    } catch (Exception e) {
      throw new BuilderException("Error creating instance. Cause: " + e, e);
    }
  }

  protected <T> Class<? extends T> resolveClass(String alias) {
    if (alias == null) return null;
    try {
      return resolveAlias(alias);// 通过别名解析
    } catch (Exception e) {
      throw new BuilderException("Error resolving class. Cause: " + e, e);
    }
  }

  protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlerAlias) {
    if (typeHandlerAlias == null) return null;
    //通过类型别名映射解析别名
    Class<?> type = resolveClass(typeHandlerAlias);
    //如果type不为null且type不为TypeHandler接口的实现类，抛出异常
    if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
      throw new BuilderException("Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
    }
    //将type强制转换成TypeHandler的实现类
    @SuppressWarnings("unchecked") // already verified it is a TypeHandler
    Class<? extends TypeHandler<?>> typeHandlerType = (Class<? extends TypeHandler<?>>) type;
    return resolveTypeHandler(javaType, typeHandlerType);
  }

  protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
    if (typeHandlerType == null)  return null;
    // javaType ignored for injected handlers see issue #746 for full detail
    //从类型处理器注册器中获取typeHandlerType类实例对应的TypeHandler对象
    TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
    //如果handler对象为null，从类型处理器注册器中获取以javaType为构造参数来构造的typeHandlerType的实例对象
    // not in registry, create a new one
    if (handler == null) handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
    return handler;
  }

  // 通过别名注册器解析别名对于的类型 Class
  protected <T> Class<? extends T> resolveAlias(String alias) {
    return typeAliasRegistry.resolveAlias(alias);
  }
}
