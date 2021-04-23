
package org.apache.ibatis.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

/**
 * 两个作用：
 * 1.将"#{xxx}" 替换为占位符  "?"
 * 2.将占位符"?"对应的属性信息一起构建成到ParameterMapping中，以便在后续ParameterHandler真正地参数赋值。
*/
public class SqlSourceBuilder extends BaseBuilder {

  private static final String PARAMETER_PROPERTIES = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

  public SqlSourceBuilder(Configuration configuration) {
    super(configuration);
  }

  public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
    // 对#{}这样的字符串内容的解析处理类 // 创建TokenHandler，用来将原始Sql中的'#{}' 解析成'?'
    ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
    // 创建sql语句解析器   美井 # 在这里解析
    GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
    // 获取真实的可执行性的sql语句 // 解析原始sql
    // select * from tbl_employee where id = #{id}  替换后  select * from tbl_employee where id = ?
    String sql = parser.parse(originalSql);
    // 包装成StaticSqlSource返回   // 创建出StaticSqlSource对象
    return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
  }

  private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {
    // 记录所有占位符对应的ParameterMapping
    private List<ParameterMapping> parameterMappings = new ArrayList<>();
    private Class<?> parameterType;
    private MetaObject metaParameters;

    public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
      super(configuration);
      this.parameterType = parameterType;
      this.metaParameters = configuration.newMetaObject(additionalParameters);
    }

    public List<ParameterMapping> getParameterMappings() {
      return parameterMappings;
    }

    // 占位符替换
    @Override
    public String handleToken(String content) {
      parameterMappings.add(buildParameterMapping(content)); // $和#  美井组合
      // 此处的作用就是对#{}节点中的key值保存映射，比如javaType/jdbcType/mode等信息，限于篇幅过长，读者可自行分析  parameterMappings.add(buildParameterMapping(content));
      // 将#{}替换为?，即一般包装成`select * form test where name=? and age=?`预表达式语句
      return "?";
    }

    // 构建ParameterMapping 包含每个占位符属性的信息，如：jdbcType、javaType、typeHandler、property等等
    private ParameterMapping buildParameterMapping(String content) {
      Map<String, String> propertiesMap = parseParameterMapping(content);
      String property = propertiesMap.get("property");
      Class<?> propertyType;
      // 获取该字段或者属性的类型
      if (metaParameters.hasGetter(property)) { // issue #448 get type from additional params
        propertyType = metaParameters.getGetterType(property);
      } else if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
        propertyType = parameterType;
      } else if (JdbcType.CURSOR.name().equals(propertiesMap.get("jdbcType"))) {
        propertyType = java.sql.ResultSet.class;
      } else if (property == null || Map.class.isAssignableFrom(parameterType)) {
        propertyType = Object.class;
      } else {
        MetaClass metaClass = MetaClass.forClass(parameterType, configuration.getReflectorFactory());
        if (metaClass.hasGetter(property)) {
          propertyType = metaClass.getGetterType(property);
        } else {
          propertyType = Object.class;
        }
      }
      // 构建ParameterMapping
      ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
      Class<?> javaType = propertyType;
      String typeHandlerAlias = null;
      for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
        String name = entry.getKey();
        String value = entry.getValue();
        // doit 是否可以使用 swith 优化？？？
        if ("javaType".equals(name)) {
          javaType = resolveClass(value);
          builder.javaType(javaType);
        } else if ("jdbcType".equals(name)) {
          builder.jdbcType(resolveJdbcType(value));
        } else if ("mode".equals(name)) {
          builder.mode(resolveParameterMode(value));
        } else if ("numericScale".equals(name)) {
          builder.numericScale(Integer.valueOf(value));
        } else if ("resultMap".equals(name)) {
          builder.resultMapId(value);
        } else if ("typeHandler".equals(name)) {
          typeHandlerAlias = value;
        } else if ("jdbcTypeName".equals(name)) {
          builder.jdbcTypeName(value);
        } else if ("property".equals(name)) {
          // Do Nothing
        } else if ("expression".equals(name)) {
          throw new BuilderException("Expression based parameters are not supported yet");
        } else {
          throw new BuilderException("An invalid property '" + name + "' was found in mapping #{" + content + "}.  Valid properties are " + PARAMETER_PROPERTIES);
        }
      }
      if (typeHandlerAlias != null) {
        builder.typeHandler(resolveTypeHandler(javaType, typeHandlerAlias));
      }
      return builder.build();
    }

    private Map<String, String> parseParameterMapping(String content) {
      try {
        return new ParameterExpression(content);
      } catch (BuilderException ex) {
        throw ex;
      } catch (Exception ex) {
        throw new BuilderException("Parsing error was found in mapping #{" + content + "}.  Check syntax #{property|(expression), var1=value1, var2=value2, ...} ", ex);
      }
    }
  }
}
